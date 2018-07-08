package beta.qlife.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import beta.qlife.R;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.utility.Util;

public class QrCodeFragment extends Fragment implements ActionbarFragment, DrawerItem {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qrcode, container, false);
        setActionbarTitle();
        return v;
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
