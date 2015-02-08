package gk.nickles.ndimes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import gk.nickles.ndimes.BillSplitterApplication;
import gk.nickles.ndimes.dataaccess.AttendeeStore;
import gk.nickles.ndimes.dataaccess.EventStore;
import gk.nickles.ndimes.dataaccess.ExpenseStore;
import gk.nickles.ndimes.dataaccess.ParticipantStore;
import gk.nickles.ndimes.dataaccess.TagStore;
import gk.nickles.ndimes.dataaccess.UserStore;
import gk.nickles.ndimes.model.Attendee;
import gk.nickles.ndimes.model.Participant;
import gk.nickles.ndimes.model.SupportedCurrency;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.model.Tag;
import gk.nickles.ndimes.model.User;
import gk.nickles.ndimes.services.SharedPreferenceService;
import gk.nickles.ndimes.services.UserService;
import gk.nickles.ndimes.ui.adapter.AttendeeAdapter;
import gk.nickles.ndimes.ui.adapter.PayerAdapter;
import gk.nickles.ndimes.ui.adapter.TagAdapter;
import gk.nickles.ndimes.ui.adapter.TagDeletedListener;
import gk.nickles.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static gk.nickles.ndimes.services.AmountCalculator.convertToCents;
import static gk.nickles.ndimes.services.AmountCalculator.convertToString;
import static gk.nickles.ndimes.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.String.format;

public class AddExpense extends RoboActivity implements TagDeletedListener {

    public static final String ARGUMENT_EXPENSE_ID = "expense_id";

    @InjectView(R.id.expense_description)
    private EditText descriptionField;

    @InjectView(R.id.tag_grid)
    private GridView tagGrid;

    @InjectView(R.id.tag_grid_container)
    private LinearLayout tagGridContainer;

    @InjectView(R.id.expense_amount)
    private EditText amountField;

    private RadioGroup radioGroup;

    private RadioButton venbtn, palbtn;



    @InjectView(R.id.payer_grid)
    private GridView payerGrid;

    @InjectView(R.id.attendees_grid)
    private GridView attendeesGrid;

    @Inject
    private EventStore eventStore;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserStore userStore;

    @Inject
    private UserService userService;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private TagStore tagStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private PayerAdapter payerAdapter;

    @Inject
    private AttendeeAdapter attendeeAdapter;

    @Inject
    private TagAdapter tagAdapter;

    private Event event;
    private Expense expense;
    private int amountCents = 0;
    private TextWatcher descriptionTextWatcher;
private Boolean derp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
        Tracker tracker = ((BillSplitterApplication) getApplication()).getTracker(
                BillSplitterApplication.TrackerName.APP_TRACKER);

        tracker.setScreenName("ch.pantas.billsplitter.ui.AddExpense");

        tracker.send(new HitBuilders.AppViewBuilder().build());






    }

    @Override
    protected void onResume() {
        super.onResume();

        extractDataFromIntent(getIntent());

        setTitle(event.getName());

        if (expense == null) {
            setUpAddScreen();
        } else {
            setUpEditScreen();
        }

        tagAdapter.setTagDeletedListener(this);

        String hintAmount = format(getString(R.string.amount_hint, event.getCurrency().getSymbol()));
        amountField.setHint(hintAmount);


        amountField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String amountInput = amountField.getText().toString();
                    amountCents = convertToCents(amountInput);
                    SupportedCurrency currency = event.getCurrency();
                    amountField.setText(currency.format(amountCents));
                } else {
                    amountField.setText(convertToString(amountCents));
                }
            }
        });

        descriptionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String tag = descriptionField.getText().toString();
                    loadTags(tag);
                    tagGridContainer.setVisibility(VISIBLE);
                } else {
                    tagGridContainer.setVisibility(GONE);
                }
            }
        });

        descriptionTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String tag = descriptionField.getText().toString();
                loadTags(tag);
                tagGridContainer.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        descriptionField.addTextChangedListener(descriptionTextWatcher);

        tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getId() == R.id.tag_item_delete) {
                    Toast.makeText(AddExpense.this, "Bok", Toast.LENGTH_LONG).show();
                }
                Tag tag = (Tag) adapterView.getItemAtPosition(i);
                tagStore.persist(tag);
                descriptionField.setText(tag.getName());
                tagGridContainer.setVisibility(GONE);
            }
        });



        payerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                selectPayer(user);
                selectAllAttendees();
            }
        });

        attendeesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                toggleAttendee(user);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        amountField.setOnFocusChangeListener(null);
        descriptionField.setOnFocusChangeListener(null);
        descriptionField.removeTextChangedListener(descriptionTextWatcher);
        tagGrid.setOnItemClickListener(null);
        payerGrid.setOnItemClickListener(null);
        attendeesGrid.setOnItemClickListener(null);
    }

    private void loadTags(String tag) {
        Tag existingTag;
        List<Tag> tags;
        if (tag == null || tag.isEmpty()) {
            tags = tagStore.getAll();
        } else {
            tags = tagStore.getTagsWithNameLike(tag);
            existingTag = tagStore.getTagWithName(tag);
            if (existingTag == null) {
                tags.add(new Tag(tag));
            }
        }

        tagAdapter.setTags(tags);
        tagGrid.setAdapter(tagAdapter);
    }


    public void onSave() {
        User payingUser = payerAdapter.getSelectedUser();
        Participant payer = participantStore.getParticipant(event.getId(), payingUser.getId());
        checkNotNull(payer);

        String description = descriptionField.getText().toString();

        // Make sure we take the value of the amount field if it still has focus
        // (in that case, the amountCents variable has not yet been updated)
        if (amountField.hasFocus()) {
            amountCents = convertToCents(amountField.getText().toString());
        }

        if (amountCents == 0) {
            amountField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        if (expense == null) {
            expense = new Expense(event.getId(), payer.getId(), description, amountCents, sharedPreferenceService.getUserId());
        } else {
            expense.setPayerId(payer.getId());
            expense.setDescription(description);
            expense.setAmount(amountCents);
        }

        expenseStore.persist(expense);

        attendeeStore.removeAll(expense.getId());

        for (User user : attendeeAdapter.getSelectedUsers()) {
            Participant participant = participantStore.getParticipant(event.getId(), user.getId());
            checkNotNull(participant);

            Attendee newAttendee = new Attendee(expense.getId(), participant.getId());
            attendeeStore.persist(newAttendee);
        }
        String bill = Integer.toString(amountCents/100);
      //  String uPhone = payingUser.getPhone();

        // find which radioButton is checked by id
        if( derp == true) {

            Intent venmoIntent = VenmoLibrary.openVenmoPayment("2346", "NickleAndDimed", "4046106603", bill, description, "charge");
            startActivityForResult(venmoIntent, 2346);

        } else {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://venmo.com/?txn=charge&recipients="+ payingUser.getPhone()+"&amount="+amountCents/100+"&note="+description+"&audience=public"));
//            startActivity(browserIntent);
            Intent myIntent = new Intent(this,
                    SampleActivity.class);
            startActivity(myIntent);

        }



        finish();
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked)
                    derp=true;
                    // Pirates are the best
                    break;
            case R.id.radio_ninjas:
                if (checked)
                    derp=false;
                    // Ninjas rule
                    break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case 2346: {
                if(resultCode == RESULT_OK) {
                    String signedrequest = data.getStringExtra("signedrequest");
                    if(signedrequest != null) {
                        VenmoLibrary.VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, "uAfy6JAHmmcXUXMLtjbgzmNsfRsHXJEm");
                        if(response.getSuccess().equals("1")) {
                            //Payment successful.  Use data from response object to display a success message
                            String note = response.getNote();
                            String amount = response.getAmount();
                        }
                    }
                    else {
                        String error_message = data.getStringExtra("error_message");
                        //An error ocurred.  Make sure to display the error_message to the user
                    }
                }
                else if(resultCode == RESULT_CANCELED) {
                    //The user cancelled the payment
                }
                break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (expense != null) {
            inflater.inflate(R.menu.edit_expense, menu);
        } else {
            inflater.inflate(R.menu.add_expense, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_delete_expense == item.getItemId()) {
            if (expense == null) return true;
            attendeeStore.removeAll(expense.getId());
            expenseStore.removeById(expense.getId());
            finish();
            return true;
        } else if (R.id.action_save_expense == item.getItemId()) {
            onSave();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onTagDelete(Tag tag) {
        tagStore.removeById(tag.getId());
        loadTags(descriptionField.getText().toString());
    }

    private void extractDataFromIntent(Intent intent) {
        checkNotNull(intent);

        UUID eventId;
        if (intent.hasExtra(ARGUMENT_EVENT_ID)) {
            eventId = (UUID) intent.getSerializableExtra(ARGUMENT_EVENT_ID);
        } else if (intent.hasExtra(ARGUMENT_EXPENSE_ID)) {
            UUID expenseId = (UUID) intent.getSerializableExtra(ARGUMENT_EXPENSE_ID);
            expense = expenseStore.getById(expenseId);
            eventId = expense.getEventId();
        } else {
            throw new IllegalStateException("Intent must either have " + ARGUMENT_EVENT_ID + " or " + ARGUMENT_EXPENSE_ID + " set.");
        }

        checkNotNull(eventId);

        event = eventStore.getById(eventId);
    }

    private void setUpEditScreen() {
        checkNotNull(expense);

        descriptionField.setText(expense.getDescription());
        amountCents = expense.getAmount();

        loadPayerList();
        Participant participantPayer = participantStore.getById(expense.getPayerId());
        User payer = userStore.getById(participantPayer.getUserId());
        selectPayer(payer);

        loadAttendeesList();
        List<Participant> attendees = attendeeStore.getAttendingParticipants(expense.getId());
        for (Participant participant : attendees) {
            User user = userStore.getById(participant.getUserId());
            attendeeAdapter.select(user);
        }
    }

    private void setUpAddScreen() {
        loadPayerList();
        User me = userService.getMe();
        checkNotNull(me);
        payerAdapter.select(me);


        loadAttendeesList();
        selectAllAttendees();
    }

    private void toggleAttendee(User user) {
        attendeeAdapter.toggle(user);
        attendeesGrid.invalidateViews();
    }

    private void selectPayer(User user) {
        payerAdapter.select(user);
        payerGrid.invalidateViews();
        loadAttendeesList();
    }

    private void selectAllAttendees() {
        attendeeAdapter.selectAll();
        attendeesGrid.invalidateViews();
    }

    private void loadAttendeesList() {
        List<Participant> attendees = participantStore.getParticipants(event.getId());

        List<User> attendingUsers = new LinkedList<User>();
        for (Participant participant : attendees) {
            attendingUsers.add(userStore.getById(participant.getUserId()));
        }

        attendeeAdapter.setUsers(attendingUsers);
        attendeesGrid.setAdapter(attendeeAdapter);
    }

    private void loadPayerList() {
        List<Participant> participants = participantStore.getParticipants(event.getId());
        List<User> users = new LinkedList<User>();
        for (Participant participant : participants) {
            users.add(userStore.getById(participant.getUserId()));
        }

        payerAdapter.setUsers(users);
        payerGrid.setAdapter(payerAdapter);
    }
}
