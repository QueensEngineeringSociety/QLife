package beta.qlife.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;

import beta.qlife.R;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.users.User;
import beta.qlife.database.local.users.UserManager;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.utility.Util;

public class QrCodeFragment extends Fragment implements ActionbarFragment, DrawerItem {

    private int curStudentNumber = -1; //used in QR generation to avoid race condition with database, and extra accesses
    private static final int STUDENT_NUMBER_LENGTH = 8;

    private View view;
    private Context context;
    private User user;
    private UserManager userManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        context = getActivity();
        setActionbarTitle();
        initNumber();
        initButtons();
        if (userNeverSetNumber()) {
            buildDialog().show();
        } else {
            generateQrCode();
        }
        return view;
    }

    private void initNumber() {
        userManager = new UserManager(context);
        ArrayList<DatabaseRow> users = userManager.getTable();
        user = (User) users.get(0); //only ever one user in database
        curStudentNumber = user.getStudentNumber();
    }

    private AlertDialog.Builder buildDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Enter your student number");
        final EditText input = new EditText(context);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                validateNumberInput(input.getText().toString());
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return alertDialog;
    }

    private void validateNumberInput(String input) {
        if (isGoodStudentNumberInput(input)) {
            useNumberInput(Integer.parseInt(input));
        } else {
            notifyBadInput();
        }
    }

    private void useNumberInput(int number) {
        curStudentNumber = number;
        user.setStudentNumber(curStudentNumber);
        userManager.updateRow(user.getId(), user);
        generateQrCode();
    }

    private void notifyBadInput() {
        Toast.makeText(context, "You entered an invalid student number", Toast.LENGTH_SHORT).show();
    }

    private void initButtons() {
        Button enterNumber = view.findViewById(R.id.enter_number);
        enterNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialog().show();
            }
        });
        Button clearNumber = view.findViewById(R.id.clear_number);
        clearNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearStudentNumber();
            }
        });
    }

    private void clearStudentNumber() {
        curStudentNumber = -1;
        user.setStudentNumber(curStudentNumber);
        userManager.updateRow(user.getId(), user);
        ImageView qrImage = view.findViewById(R.id.qrcode_pic);
        qrImage.setImageBitmap(null);
    }

    private void generateQrCode() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(String.valueOf(curStudentNumber), BarcodeFormat.QR_CODE, 256, 256);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) view.findViewById(R.id.qrcode_pic)).setImageBitmap(bmp);
        } catch (WriterException e) {
        }
    }

    private boolean isGoodStudentNumberInput(String numberInput) {
        try {
            int temp = Integer.parseInt(numberInput);
        } catch (NumberFormatException e) {
            return false;
        }
        return numberInput.length() == STUDENT_NUMBER_LENGTH;
    }

    private boolean userNeverSetNumber() {
        return curStudentNumber == -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
    }

    @Override
    public void onPause() {
        super.onPause();
        deselectDrawer();
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_qrcode), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, true);
    }
}
