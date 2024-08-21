package depth.mvp.ns.domain.report.service;

import com.querydsl.core.Tuple;
import depth.mvp.ns.domain.board.domain.Board;
import depth.mvp.ns.domain.board.domain.repository.BoardRepository;
import depth.mvp.ns.domain.user_point.domain.UserPoint;
import depth.mvp.ns.domain.user_point.domain.repository.UserPointRepository;
import depth.mvp.ns.domain.report.domain.Report;
import depth.mvp.ns.domain.report.domain.WordCount;
import depth.mvp.ns.domain.report.domain.repository.ReportRepository;
import depth.mvp.ns.domain.report.domain.repository.WordCountRepository;
import depth.mvp.ns.domain.report.dto.response.PrevReportRes;
import depth.mvp.ns.domain.report.dto.response.ReportRes;
import depth.mvp.ns.domain.report_detail.domain.ReportDetail;
import depth.mvp.ns.domain.report_detail.domain.ReportType;
import depth.mvp.ns.domain.report_detail.domain.repository.ReportDetailRepository;
import depth.mvp.ns.domain.s3.service.S3Uploader;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme.domain.repository.ThemeRepository;
import depth.mvp.ns.domain.user.domain.User;
import depth.mvp.ns.domain.user.domain.repository.UserRepository;
import depth.mvp.ns.global.config.security.token.CustomUserDetails;
import depth.mvp.ns.global.payload.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.collection.Seq;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ThemeRepository themeRepository;
    private final BoardRepository boardRepository;
    private final WordCountRepository wordCountRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;
    private final CloudImageGenerator cloudImageGenerator;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;


    public ResponseEntity<?> findReport(CustomUserDetails customUserDetails, LocalDate parsedDate) {
        Optional<Theme> optionalTheme = themeRepository.findByDate(parsedDate);
        LocalDate today = LocalDate.now();

        if (optionalTheme.isEmpty() && parsedDate.equals(today)) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .check(false)
                    .information("No theme found for the given date.")
                    .build();
            return ResponseEntity.ok(apiResponse);
        }

        if (optionalTheme.isEmpty()) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .check(false)
                    .information("No theme found for the given date.")
                    .build();
            return ResponseEntity.ok(apiResponse);
        }

        Theme theme = optionalTheme.get();
        log.info("Theme found: {}", theme.getContent());


        Optional<Report> optionalReport = reportRepository.findByTheme(theme);

        if (optionalReport.isEmpty() && parsedDate.equals(today) ) {
            // 오늘자 레포트 조회


            int writtenTotal = reportRepository.getBoardCount(theme);
            Board longestBoardByTheme = boardRepository.findLongestBoardByTheme(theme);
            System.out.println("longestBoardByTheme.getUser().getId() = " + longestBoardByTheme.getUser().getId());

            ReportRes.LongestWriter longestWriter = null;
            if (longestBoardByTheme != null && longestBoardByTheme.getUser() != null) {
                User user = longestBoardByTheme.getUser();
                boolean isCurrentUser = customUserDetails != null && customUserDetails.getId().equals(user.getId());
                longestWriter = new ReportRes.LongestWriter(
                        user.getId(),
                        isCurrentUser,
                        user.getNickname(),
                        user.getImageUrl(),
                        longestBoardByTheme.getLength()
                );
            }

            ReportRes reportRes = ReportRes.builder()
                    .selectedDate(parsedDate)
                    .themeName(theme.getContent())
                    .writtenTotal(writtenTotal)
                    .longestWriter(customUserDetails != null ? longestWriter : null) // 로그인하지 않은 경우 longestWriter 정보를 제공하지 않음
                    .build();

            return ResponseEntity.ok(reportRes);
        } else {
            // 과거 레포트 조회

            Report report = null;

            if (optionalReport.isPresent()) {
                log.info("Existing report found for theme: {}", theme.getContent());

                report = optionalReport.get();

            }
            Board longestBoardByTheme = boardRepository.findLongestBoardByTheme(theme);
            User user = longestBoardByTheme.getUser();


            List<ReportDetail> allBestReportTypeByReport = reportDetailRepository.findAllBestReportTypeByReport(report);
            log.info("Number of best report details found: {}", allBestReportTypeByReport.size());

            if (allBestReportTypeByReport.isEmpty()) {
                log.warn("No best report details found for the given report.");
            }else {
                allBestReportTypeByReport.forEach(detail -> {
                    log.info("ReportDetail found: ReportType={}, UserId={}", detail.getReportType(), detail.getUser().getId());
                });}

            Long bestSelectedCountByUserId = customUserDetails != null
                    ? reportDetailRepository.findBestSelectedCountByUserId(customUserDetails.getId())
                    : 0;



            int writtenTotal = reportRepository.getBoardCount(theme);
            log.info("Total written count: {}", writtenTotal);



            List<PrevReportRes.BestPost> bestPosts = allBestReportTypeByReport.stream()
                    .map(reportDetail -> {
                        Tuple mostLikedBoardInfo = boardRepository.findMostLikedBoardCountAndTitleWithUserAndTheme(reportDetail.getUser(), theme);


                        boolean isCurrentUser = customUserDetails != null && customUserDetails.getId().equals(reportDetail.getUser().getId());


                        Long bestSelectedCountByUserId1 = reportDetailRepository.findBestSelectedCountByUserId(reportDetail.getUser().getId());

                        Long boardId = reportDetail.getBoard().getId(); // BEST로 선정된 게시글의 ID
                        boolean isLiked = customUserDetails != null && boardRepository.isBoardLikedByUser(boardId, customUserDetails.getId());



                        int likeCount = boardRepository.countLikesByBoardId(reportDetail.getBoard().getId());
                        long likeCount2 = likeCount;

                        if (mostLikedBoardInfo == null) {
                            log.warn("No valid title found for user: {}", reportDetail.getUser().getNickname());

                            return PrevReportRes.BestPost.builder()
                                    .isCurrentUser(isCurrentUser)
                                    .userId(reportDetail.getUser().getId())
                                    .nickname(reportDetail.getUser().getNickname())
                                    .imageUrl(reportDetail.getUser().getImageUrl())
                                    .title("유효한 제목 없음.")
                                    .likeCount(likeCount2)
                                    .bestSelectionCount(bestSelectedCountByUserId1)
                                    .boardCreatedAt(reportDetail.getBoard().getCreatedDate())
                                    .boardId(reportDetail.getBoard().getId())
                                    .isLiked(isLiked)
                                    .build();
                        }





                        String title = mostLikedBoardInfo.get(1, String.class);


                        log.info("Best post found: title={}, likes={}, user={}", title, likeCount, reportDetail.getUser().getNickname());




                        return PrevReportRes.BestPost.builder()
                                .isCurrentUser(isCurrentUser)
                                .userId(reportDetail.getUser().getId())
                                .nickname(reportDetail.getUser().getNickname())
                                .imageUrl(reportDetail.getUser().getImageUrl())
                                .title(title)
                                .likeCount(likeCount2)
                                .bestSelectionCount(bestSelectedCountByUserId1)
                                .boardCreatedAt(reportDetail.getBoard().getCreatedDate())
                                .boardId(reportDetail.getBoard().getId())
                                .isLiked(isLiked)
                                .build();

                    })
                    .collect(Collectors.toList());

            log.info("Total best posts found: {}", bestPosts.size());


            return ResponseEntity.ok(PrevReportRes.builder()
                    .selectedDate(parsedDate)
                    .themeName(theme.getContent())
                    .writtenTotal(writtenTotal)
                    .wordCloud(report.getWordCloud())
                    .topWord(report.getTopWord())
                    .count(report.getCount())
                    .longestWriter(new ReportRes.LongestWriter(
                            user.getId(),
                            customUserDetails != null && customUserDetails.getId().equals(user.getId()),
                            user.getNickname(),
                            user.getImageUrl(),
                            longestBoardByTheme.getLength()
                    ))
                    .bestPost(bestPosts)
                    .build());
        }
    }


    @Transactional
    public void generateNReport() {
        LocalDate today = LocalDate.now();
        Theme theme = themeRepository.findByDate(today)
                .orElseThrow(EntityNotFoundException::new);

        List<Board> boardList = boardRepository.findByTheme(theme);


        // 그 날 테마로 작성된 글 갯 수
        int writtenBoardCount = boardList.size();

        Map<String, Integer> wordCount = new HashMap<>();

        // 워드 카운트해서 wordcount 디비에 저장 + 가장 많이 쓴 단어 report에 저장 + 몇 번 노출됐는지도 저장
        for (Board board : boardList) {

            String content = board.getContent();
            List<String> nouns = extractNouns(content);

            System.out.println("Board ID: " + board.getId() + ", Nouns: " + nouns);

            for (String noun : nouns) {
                wordCount.put(noun, wordCount.getOrDefault(noun, 0) + 1);
            }
        }

        // 가장 길게 글 쓴 사람 찾기
        Board longestBoard = boardRepository.findLongestBoardByTheme(theme);

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
                .wordCloud(null)
                .build();


        // WordCount 객체 저장
        wordCount.forEach((word, count) -> {
            WordCount wordCountEntity = WordCount.builder()
                    .word(word)
                    .count(count)
                    .report(report)
                    .build();
            wordCountRepository.save(wordCountEntity);
        });


        //=============================================================================================//
        //워드 클라우드 생성
        List<WordCount> wordCountEntities = wordCount.entrySet().stream()
                .map(entry -> new WordCount(entry.getKey(), entry.getValue(), report))
                .collect(Collectors.toList());

        BufferedImage wordCloudImage = cloudImageGenerator.generateImage(wordCountEntities, System.currentTimeMillis());

        String wordCloudUrl = uploadImageToS3(wordCloudImage, "wordcloud-" + UUID.randomUUID() + ".png");


        report.updateWordCloud(wordCloudUrl);
        reportRepository.save(report);

        //=============================================================================================//


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
        List<Board> top3BoardWithMostLiked = boardRepository.findTop3BoardWithMostLiked(theme);

        // 베스트 게시글 선정 포인트 각 +5점
        top3BoardWithMostLiked.stream().map(
                board -> {
                    Long userId = board.getUser().getId();
                    User user = userRepository.findById(userId)
                            .orElseThrow(EntityNotFoundException::new);

                    user.addPoint(5);

                    UserPoint userPoint = UserPoint.builder()
                            .user(user)
                            .score(5)
                            .build();

                    userPointRepository.save(userPoint);
                    return board;
                }
        ).collect(Collectors.toList());

        top3BoardWithMostLiked.forEach(board -> {
            // ReportDetail 엔티티에 topLikedBoard 정보 저장
            ReportDetail reportDetail = ReportDetail.builder()
                    .reportType(ReportType.BEST)
                    .user(board.getUser())
                    .report(report)
                    .board(board)
                    .build();
            reportDetailRepository.save(reportDetail);
        });


    }


    public List<String> extractNouns(String text) {
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
        List<String> nouns = tokenList.stream()
                .filter(token -> token.getPos().toString().equals("Noun") && token.getText().length() > 1)
                .map(KoreanTokenJava::getText)
                .collect(Collectors.toList());

        // 영어 단어 추출
        Pattern pattern = Pattern.compile("\\b[A-Za-z]+\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String word = matcher.group();
            if (word.length() > 1) { // 한 글자 단어 제외
                nouns.add(word);
            }
        }

        System.out.println("Extracted Nouns: " + nouns);
        return nouns;

    }

    private String uploadImageToS3(BufferedImage image, String fileName) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            // S3Uploader 클래스를 사용하여 S3에 이미지 업로드
            return s3Uploader.uploadBufferedImage(new ByteArrayInputStream(bytes), fileName, bytes.length, "image/png");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }
}