package gk.nickles.ndimes.ui.fragment;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.apps.common.testing.ui.espresso.DataInteraction;

import org.hamcrest.Matchers;
import org.mockito.Mock;

import gk.nickles.ndimes.dataaccess.AttendeeStore;
import gk.nickles.ndimes.dataaccess.ParticipantStore;
import gk.nickles.ndimes.model.Debt;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.User;
import gk.nickles.ndimes.services.ExpenseService;
import gk.nickles.splitty.R;

import static gk.nickles.ndimes.model.SupportedCurrency.CHF;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DebtsFragmentTest extends BaseEventDetailsFragmentTest {

    private static final User JOE = new User(randomUUID(), "Joe");
    private static final User MARY = new User(randomUUID(), "Mary");

    @Mock
    private ExpenseService expenseService;

    @Mock
    private AttendeeStore attendeeStore;

    @Mock
    private ParticipantStore participantStore;

    @SmallTest
    public void testCorrectDebtsAreShown() {
        // Given
        Debt debt = new Debt(JOE, MARY, 100, CHF);
        when(debtCalculator.calculateDebts(event)).thenReturn(asList(debt));

        // When
        getActivity();

        // Then
        onDebt(debt).check(matches(isDisplayed()));
    }

    @SmallTest
    public void testClickingOnOwnDebtOpensDialog(){
        // Given
        Debt debt = new Debt(JOE, me, 100, CHF);
        when(debtCalculator.calculateDebts(event)).thenReturn(asList(debt));
        getActivity();

        // When
        onDebt(debt).perform(click());

        // Then
        onView(withText(R.string.mark_debt_as_paid_title)).check(matches(isDisplayed()));
    }

    @SmallTest
    public void testClickingOnForeignDebtDoesNotOpenDialog(){
        // Given
        Debt debt = new Debt(JOE, MARY, 100, CHF);
        when(debtCalculator.calculateDebts(event)).thenReturn(asList(debt));
        getActivity();

        // When
        onDebt(debt).perform(click());

        // Then
        onView(withText(R.string.mark_debt_as_paid_title)).check(doesNotExist());
    }

    @SmallTest
    public void testClickingNoOnPayBackDialogClosesDialog(){
        // Given
        Debt debt = new Debt(JOE, me, 100, CHF);
        when(debtCalculator.calculateDebts(event)).thenReturn(asList(debt));
        getActivity();
        onDebt(debt).perform(click());

        // When
        onView(withText(R.string.no)).perform(click());

        // Then
        onView(withText(R.string.mark_debt_as_paid_title)).check(doesNotExist());
        verify(expenseService, never()).createPaybackExpense(any(Debt.class), any(Event.class));
    }

    @SmallTest
    public void testClickingYesOnPayBackDialogCreatesExpenseAndClosesDialog(){
        // Given
        Debt debt = new Debt(JOE, me, 100, CHF);
        when(debtCalculator.calculateDebts(event)).thenReturn(asList(debt));
        getActivity();
        onDebt(debt).perform(click());

        // When
        onView(withText(R.string.yes)).perform(click());

        // Then
        onView(withText(R.string.mark_debt_as_paid_title)).check(doesNotExist());
        verify(expenseService, times(1)).createPaybackExpense(debt, event);
    }

    private DataInteraction onDebt(Debt debt){
        return onData(Matchers.<Object>equalTo(debt)).inAdapterView(withId(R.id.debts_list));
    }
}
