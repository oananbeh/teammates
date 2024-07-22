package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.RemindFeedbackSessionResultAction;

/**
 * SUT: {@link RemindFeedbackSessionResultAction}.
 */
public class RemindFeedbackSessionResultActionTest extends BaseActionTest<RemindFeedbackSessionResultAction> {

    private Course course;
    private Instructor instructor;
    private Student student;
    private Instant nearestHour;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        nearestHour = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);

        course = generateCourse1();
        instructor = generateInstructor1InCourse(course);
        student = generateStudent1InCourse(course);

        loginAsInstructor(instructor.getGoogleId());

        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
    }

    @Test
    protected void testExecute_feedbackSessionNotPublished_warningMessage() {
        FeedbackSession unpublishedFeedbackSession = generateUnpublishedSessionInCourse(course, instructor);

        when(mockLogic.getFeedbackSession(isA(String.class), isA(String.class)))
                .thenReturn(unpublishedFeedbackSession);

        String[] paramsFeedbackSessionNotPublished = new String[] {
                Const.ParamsNames.COURSE_ID, unpublishedFeedbackSession.getCourse().getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, unpublishedFeedbackSession.getName(),
        };

        String[] usersToRemind = {instructor.getEmail(), student.getEmail()};
        FeedbackSessionRespondentRemindRequest remindRequest = new FeedbackSessionRespondentRemindRequest();
        remindRequest.setUsersToRemind(usersToRemind);

        InvalidOperationException ioe = verifyInvalidOperation(remindRequest, paramsFeedbackSessionNotPublished);
        assertEquals("Published email could not be resent "
                + "as the feedback session is not published.", ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    protected void testExecute_feedbackSessionPublished_success() {
        FeedbackSession publishedFeedbackSession = generatePublishedSessionInCourse(course, instructor);

        when(mockLogic.getFeedbackSession(isA(String.class), isA(String.class)))
                .thenReturn(publishedFeedbackSession);

        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, publishedFeedbackSession.getCourse().getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, publishedFeedbackSession.getName(),
        };

        String[] usersToRemind = {instructor.getEmail(), student.getEmail()};
        FeedbackSessionRespondentRemindRequest remindRequest = new FeedbackSessionRespondentRemindRequest();
        remindRequest.setUsersToRemind(usersToRemind);

        RemindFeedbackSessionResultAction validAction = getAction(remindRequest, paramsTypical);
        getJsonResult(validAction);

        verifySpecifiedTasksAdded(Const.TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_QUEUE_NAME, 1);
    }

    private Course generateCourse1() {
        Course c = new Course("course-1", "Typical Course 1",
                "Africa/Johannesburg", "TEAMMATES Test Institute 0");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return c;
    }

    private Instructor generateInstructor1InCourse(Course courseInstructorIsIn) {
        return new Instructor(courseInstructorIsIn, "instructor-1",
                "instructor-1@tm.tmt", false,
                "", null,
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER));
    }

    private Student generateStudent1InCourse(Course courseStudentIsIn) {
        String email = "student1@gmail.com";
        String name = "student-1";
        String googleId = "student-1";
        Student s = new Student(courseStudentIsIn, name, email, "comment for student-1");
        s.setAccount(new Account(googleId, name, email));
        return s;
    }

    private FeedbackSession generatePublishedSessionInCourse(Course course, Instructor instructor) {
        Instant beforeNow = nearestHour.minus(3, java.time.temporal.ChronoUnit.HOURS);
        FeedbackSession fs = new FeedbackSession("published-feedback-session", course,
                instructor.getEmail(), "generic instructions",
                beforeNow, beforeNow,
                beforeNow, beforeNow,
                Duration.ofHours(10), true, false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }

    private FeedbackSession generateUnpublishedSessionInCourse(Course course, Instructor instructor) {
        Instant afterNowStartTime = nearestHour.plus(10, java.time.temporal.ChronoUnit.HOURS);
        Instant afterNowEndTime = nearestHour.plus(15, java.time.temporal.ChronoUnit.HOURS);
        FeedbackSession fs = new FeedbackSession("unpublished-feedback-session", course,
                instructor.getEmail(), "generic instructions",
                afterNowStartTime,
                afterNowEndTime,
                afterNowStartTime, afterNowEndTime,
                Duration.ofHours(10), true, false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }
}
