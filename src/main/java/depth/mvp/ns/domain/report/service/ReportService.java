package depth.mvp.ns.domain.report.service;

import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.board_like.domain.repository.BoardLikeRepository;
import depth.mvp.ns.domain.report.domain.Report;
import depth.mvp.ns.domain.report.domain.WordCount;
import depth.mvp.ns.domain.report.domain.repository.ReportRepository;
import depth.mvp.ns.domain.report.domain.repository.WordCountRepository;
import depth.mvp.ns.domain.report_detail.domain.ReportDetail;
import depth.mvp.ns.domain.report_detail.domain.ReportType;
import depth.mvp.ns.domain.report_detail.domain.repository.ReportDetailRepository;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.collection.Seq;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ThemeRepository themeRepository;
    private final BoardRepository boardRepository;
    private final WordCountRepository wordCountRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;
    private final BoardLikeRepository boardLikeRepository;

    @Transactional
    public void generateNReport() {
        LocalDate today = LocalDate.now();
        Theme theme = themeRepository.findByDate(today)
                .orElseThrow(EntityNotFoundException::new);

        List<Board> boardList = boardRepository.findByTheme(theme);


        // 그 날 테마로 작성된 글 갯 수
        int writtenBoardCount = boardList.size();

        Map<String, Integer> wordCount = new HashMap<>();
        Board longestBoard = null; // 가장 길게 글 쓴 사람을 찾기 위한 변수

        // 워드 카운트해서 wordcount 디비에 저장 + 가장 많이 쓴 단어 report에 저장 + 몇 번 노출됐는지도 저장
        for (Board board : boardList) {

            String content = board.getContent();
            List<String> nouns = extractNouns(content);

            System.out.println("Board ID: " + board.getId() + ", Nouns: " + nouns);

            for (String noun : nouns) {
                wordCount.put(noun, wordCount.getOrDefault(noun, 0) + 1);
            }

            // 가장 길게 글 쓴 사람 찾기
            if (longestBoard == null || content.length() > longestBoard.getContent().length()) {
                longestBoard = board;
            }


        }

        // 가장 많이 사용된 단어 찾기
        String topWord = wordCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        int topWordCount = wordCount.getOrDefault(topWord, 0);


        //=============================================================================================//



        Report report = Report.builder()
                .total(writtenBoardCount)
                .topWord(topWord)
                .count(topWordCount)
                .theme(theme)
                .build();

        reportRepository.save(report);


        // WordCount 객체 저장 -> 불필요 시 추후 삭제 할 수 있음.
        wordCount.forEach((word, count) -> {
            WordCount wordCountEntity = WordCount.builder()
                    .word(word)
                    .count(count)
                    .report(report)
                    .build();
            wordCountRepository.save(wordCountEntity);
        });


        // 가장 길게 글 쓴 사람 저장
        if (longestBoard != null) {
            ReportDetail reportDetail = ReportDetail.builder()
                    .reportType(ReportType.LONGEST)
                    .user(longestBoard.getUser())
                    .report(report)
                    .build();
            reportDetailRepository.save(reportDetail);
        }

        // 가장 좋아요 많이 받은 top3 게시글 저장
        List<Board> top3BoardWithMostLiked = boardRepository.findTop3BoardWithMostLiked();
        top3BoardWithMostLiked.forEach(board -> {
            // ReportDetail 엔티티에 topLikedBoard 정보 저장
            ReportDetail reportDetail = ReportDetail.builder()
                    .reportType(ReportType.BEST)
                    .user(board.getUser())
                    .report(report)
                    .build();
            reportDetailRepository.save(reportDetail);
        });


    }


    public List<String> extractNouns(String text) {
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
        List<String> nouns = tokenList.stream()
                .filter(token -> token.getPos().toString().equals("Noun"))
                .map(KoreanTokenJava::getText)
                .collect(Collectors.toList());

        // 영어 단어 추출
        Pattern pattern = Pattern.compile("\\b[A-Za-z]+\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            nouns.add(matcher.group());
        }

        System.out.println("Extracted Nouns: " + nouns);
        return nouns;

    }
}
