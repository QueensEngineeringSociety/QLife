package engsoc.qlife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import engsoc.qlife.R;
import engsoc.qlife.utility.Util;
import engsoc.qlife.interfaces.IQLOptionsMenuActivity;

/**
 * Created by Carson on 06/06/2017.
 * Displays text providing information about the app.
 */
public class AboutActivity extends AppCompatActivity implements IQLOptionsMenuActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setBackButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        handleOptionsClick(item.getItemId());
        return true;
    }

    @Override
    public void setBackButton() {
        Util.setBackButton(getSupportActionBar());
    }

    @Override
    public void handleOptionsClick(int itemId) {
        switch (itemId) {
            case R.id.settings:
                Intent settings = new Intent(AboutActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.review:
                startActivity(new Intent(AboutActivity.this, ReviewActivity.class));
                break;
            case android.R.id.home:
                finish();
        }
    }

    @Override
    public void inflateOptionsMenu(Menu menu) {
        Util.inflateOptionsMenu(R.menu.about_menu, menu, getMenuInflater());
    }
}
