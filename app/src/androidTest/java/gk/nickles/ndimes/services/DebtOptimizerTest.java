package gk.nickles.ndimes.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.List;

import gk.nickles.ndimes.framework.BaseMockitoInstrumentationTest;
import gk.nickles.ndimes.model.Debt;
import gk.nickles.ndimes.model.User;

import static gk.nickles.ndimes.framework.CustomMatchers.matchesDebt;
import static gk.nickles.ndimes.model.SupportedCurrency.EUR;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public class DebtOptimizerTest extends BaseMockitoInstrumentationTest {

    @Inject
    private DebtOptimizer debtOptimizer;

    private User joe;
    private User dave;
    private User mary;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        joe = new User(randomUUID(), "Joe");
        dave = new User(randomUUID(), "Dave");
        mary = new User(randomUUID(), "Mary");
    }

    @SmallTest
    public void testCalculateDebtsEliminates2PersonCircle() {
        // Given
        List<Debt> debts = asList(debt(joe, dave, 20), debt(dave, joe, 20));

        // When
        List<Debt> result = debtOptimizer.optimize(debts, EUR);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @SmallTest
    public void testCalculateDebtsOptimizes2PersonCircle() {
        // Given
        List<Debt> debts = asList(debt(joe, dave, 20), debt(dave, joe, 10));

        // When
        List<Debt> result = debtOptimizer.optimize(debts, EUR);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertThat(result, hasItem(matchesDebt(joe, dave, 10)));
    }

    @SmallTest
    public void testCalculateDebtsEliminatesTransitiveDebts() {
        // Given
        List<Debt> debts = asList(debt(joe, dave, 20), debt(dave, mary, 20));

        // When
        List<Debt> result = debtOptimizer.optimize(debts, EUR);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertThat(result, hasItem(matchesDebt(joe, mary, 20)));
    }

    @SmallTest
    public void testCalculateDebtsEliminatesDoublePaths() {
        // Given
        List<Debt> debts = asList(debt(joe, dave, 20), debt(joe, mary, 20), debt(mary, dave, 20));

        // When
        List<Debt> result = debtOptimizer.optimize(debts, EUR);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertThat(result, hasItem(matchesDebt(joe, dave, 40)));
    }

    @SmallTest
    public void testCalculateDebtsWithOnePersonPayingTwoPersonsCanNotBeOptimized() {
        // Given
        List<Debt> debts = asList(debt(joe, dave, 10), debt(joe, mary, 10));

        // When
        List<Debt> result = debtOptimizer.optimize(debts, EUR);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result, hasItem(matchesDebt(joe, dave, 10)));
        assertThat(result, hasItem(matchesDebt(joe, mary, 10)));
    }

    @SmallTest
    public void testCalculateDebtsWithTwoPersonsPayingOnePersonCanNotBeOptimized() {
        // Given
        List<Debt> debts = asList(debt(joe, dave, 10), debt(mary, dave, 10));

        // When
        List<Debt> result = debtOptimizer.optimize(debts, EUR);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result, hasItem(matchesDebt(joe, dave, 10)));
        assertThat(result, hasItem(matchesDebt(mary, dave, 10)));
    }

    private static Debt debt(User from, User to, int amount) {
        return new Debt(from, to, amount, EUR);
    }
}
