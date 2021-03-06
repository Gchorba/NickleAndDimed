package gk.nickles.ndimes.ui;

import android.bluetooth.BluetoothAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.UUID;

import gk.nickles.ndimes.dataaccess.EventStore;
import gk.nickles.ndimes.dataaccess.ParticipantStore;
import gk.nickles.ndimes.dataaccess.UserStore;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.remote.SimpleBluetoothServer;
import gk.nickles.ndimes.services.ExportService;
import gk.nickles.ndimes.services.ImportService;
import gk.nickles.ndimes.services.UserService;
import gk.nickles.ndimes.services.datatransfer.EventDto;
import gk.nickles.ndimes.services.datatransfer.EventDtoOperator;
import gk.nickles.splitty.R;

import static android.nfc.NdefRecord.createMime;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static gk.nickles.ndimes.remote.SimpleBluetoothServer.BluetoothListener;
import static gk.nickles.ndimes.services.datatransfer.EventDtoBuilder.convertToJson;
import static roboguice.RoboGuice.getInjector;

public class BeamEvent extends BeamBaseActivity implements BluetoothListener {

    public static final String ARGUMENT_EVENT_ID = "event_id";

    @Inject
    private EventStore eventStore;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private UserStore userStore;
    @Inject
    private UserService userService;
    @Inject
    private ImportService importService;
    @Inject
    private ExportService exportService;


    private Event event;

    private SimpleBluetoothServer bluetoothServer;

    @Override
    protected int getContentView() {
        return R.layout.beam_event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UUID eventId = (UUID) getIntent().getSerializableExtra(ARGUMENT_EVENT_ID);
        event = eventStore.getById(eventId);
        String titleTemplate = getString(R.string.beam_event_title_template);
        setTitle(String.format(titleTemplate, event.getName()));
        setUpForBeam();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bluetoothServer != null) {
            bluetoothServer.cancel();
            finish();
        }
    }

    @Override
    protected void startBeaming(NfcAdapter nfcAdapter, BluetoothAdapter bluetoothAdapter) {
        setUpTapScreen();
        startBluetoothServer(nfcAdapter, bluetoothAdapter);
    }

    private void setUpTapScreen() {
        showMessage(R.string.beam_event_message, R.drawable.ic_beam_touch);
    }


    private void startBluetoothServer(NfcAdapter nfcAdapter, BluetoothAdapter bluetoothAdapter) {
        nfcAdapter.setNdefPushMessage(createNdefMessage(bluetoothAdapter.getAddress()), this);
        bluetoothServer = getInjector(this).getInstance(SimpleBluetoothServer.class).init(bluetoothAdapter, this);
        bluetoothServer.start();
    }

    private static NdefMessage createNdefMessage(String text) {
        return new NdefMessage(new NdefRecord[]{
                createMime(
                        "application/vnd.com.example.android.beam",
                        text.getBytes())
        });
    }

    @Override
    public void onMessageReceived(String message) {
        Gson gson = new Gson();
        EventDtoOperator eventDto = new EventDtoOperator(gson.fromJson(message, EventDto.class));
        importService.importEvent(eventDto);
        setUpSuccessScreen();
    }

    @Override
    public void onConnected() {

        EventDto eventDto = exportService.exportEvent(event.getId());
        bluetoothServer.postMessage(convertToJson(eventDto));
        setUpWaitingScreen();
    }

    @Override
    public void onCommunicationError(Exception e) {
        setUpCommunicationErrorScreen();
    }
}