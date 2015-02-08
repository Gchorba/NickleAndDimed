package gk.nickles.ndimes.ui.fragment;

import android.content.Intent;

import org.mockito.Mock;

import gk.nickles.ndimes.dataaccess.EventStore;
import gk.nickles.ndimes.dataaccess.ExpenseStore;
import gk.nickles.ndimes.framework.BaseEspressoTest;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.model.SupportedCurrency;
import gk.nickles.ndimes.model.User;
import gk.nickles.ndimes.services.ActivityStarter;
import gk.nickles.ndimes.services.DebtCalculator;
import gk.nickles.ndimes.services.SharedPreferenceService;
import gk.nickles.ndimes.services.UserService;
import gk.nickles.ndimes.ui.EventDetails;

import static gk.nickles.ndimes.ui.EventDetails.ARGUMENT_EVENT_ID;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.when;

public abstract class BaseEventDetailsFragmentTest extends BaseEspressoTest<EventDetails> {

    @Mock
    protected SharedPreferenceService sharedPreferenceService;

    @Mock
    protected ActivityStarter activityStarter;

    @Mock
    protected EventStore eventStore;

    @Mock
    protected DebtCalculator debtCalculator;

    @Mock
    protected UserService userService;

    @Mock
    protected ExpenseStore expenseStore;

    protected Event event;

    protected User me;

    protected Expense expense;

    public BaseEventDetailsFragmentTest() {
        super(EventDetails.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        me = new User(randomUUID(), "Me");
        when(userService.getMe()).thenReturn(me);

        event = new Event(randomUUID(), "An event", SupportedCurrency.CHF, randomUUID());
        when(eventStore.getById(event.getId())).thenReturn(event);

        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);

        // Ensures that help text is not shown
        expense = new Expense(randomUUID(), event.getId(), me.getId(), "An expense", 100, me.getId());
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(asList(expense));
    }
}