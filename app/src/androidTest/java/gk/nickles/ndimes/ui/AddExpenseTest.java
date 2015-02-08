package gk.nickles.ndimes.ui;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;

import gk.nickles.ndimes.dataaccess.AttendeeStore;
import gk.nickles.ndimes.dataaccess.EventStore;
import gk.nickles.ndimes.dataaccess.ExpenseStore;
import gk.nickles.ndimes.dataaccess.ParticipantStore;
import gk.nickles.ndimes.dataaccess.UserStore;
import gk.nickles.ndimes.framework.BaseEspressoTest;
import gk.nickles.ndimes.model.Attendee;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.model.Participant;
import gk.nickles.ndimes.model.User;
import gk.nickles.ndimes.services.ActivityStarter;
import gk.nickles.ndimes.services.SharedPreferenceService;
import gk.nickles.ndimes.services.UserService;
import gk.nickles.ndimes.ui.adapter.AttendeeAdapter;
import gk.nickles.ndimes.ui.adapter.PayerAdapter;

import static gk.nickles.ndimes.model.SupportedCurrency.EUR;
import static gk.nickles.ndimes.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddExpenseTest extends BaseEspressoTest<AddExpense> {
    public AddExpenseTest() {
        super(AddExpense.class);
    }

    @Mock
    private EventStore eventStore;

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private UserStore userStore;

    @Mock
    private UserService userService;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private AttendeeStore attendeeStore;

    @Mock
    private ActivityStarter activityStarter;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Mock
    private PayerAdapter payerAdapter;

    @Mock
    private AttendeeAdapter attendeeAdapter;

    private User me;
    private Participant participantMe;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event(randomUUID(), "An Event", EUR, randomUUID());
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);
        me = new User(randomUUID(), "Me");
        when(userService.getMe()).thenReturn(me);
        participantMe = new Participant(randomUUID(), me.getId(), event.getId(), true, 0);
        when(participantStore.getParticipant(event.getId(), me.getId())).thenReturn(participantMe);
        when(userStore.getUserWithName(me.getName())).thenReturn(me);
        when(payerAdapter.getSelectedUser()).thenReturn(me);

        when(payerAdapter.getViewTypeCount()).thenReturn(1);
        when(attendeeAdapter.getViewTypeCount()).thenReturn(1);
    }

    @LargeTest
    public void testTitleIsEventName() {
        // When
        getActivity();

        // Then
        onView(withText(event.getName())).check(matches(isDisplayed()));
    }

    private static Matcher<Attendee> newAttendeeWithUserId(final String participantId) {
        return new TypeSafeMatcher<Attendee>() {
            @Override
            public boolean matchesSafely(Attendee attendee) {
                return attendee.isNew()
                        && participantId.equals(attendee.getParticipant());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("New Attendee with user ");
                description.appendText(participantId);
            }
        };
    }

    private static Matcher<Expense> newExpenseWith(final String expenseDescription, final int amount, final String eventId, final String participantId) {
        return new TypeSafeMatcher<Expense>() {
            @Override
            public boolean matchesSafely(Expense expense) {
                if (!expense.isNew()) return false;
                if (!expenseDescription.equals(expense.getDescription())) return false;
                if (amount != expense.getAmount()) return false;
                if (!eventId.equals(expense.getEventId())) return false;
                if (participantId != null && !participantId.equals(expense.getPayerId()))
                    return false;

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("New expense with description ");
                description.appendText(expenseDescription);
                description.appendText(" and amount ");
                description.appendValue(amount);
                description.appendText(" and event ");
                description.appendText(eventId);
            }
        };
    }
}
