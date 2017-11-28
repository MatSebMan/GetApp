package ar.com.vittal.vittalgetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class DisclaimerActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int NUM_PAGES = 3;
    private static final String SHAPRE_NAME = "ar.com.vittal.vittalgetapp";
    private static final String LEGAL_KEY = "disclaimerAgreementAccepted";
    private static SharedPreferences shaPre;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.shaPre = getSharedPreferences(SHAPRE_NAME, MODE_PRIVATE);
        if (this.shaPre.contains(LEGAL_KEY))
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_disclaimer);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static DisclaimerActivity _activity;
        private Button button;
        private CheckBox checkBox;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, DisclaimerActivity activity) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            _activity = activity;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_disclaimer, container, false);
            checkBox = (CheckBox) rootView.findViewById(R.id.disclaimer_checkbox);
            checkBox.setOnClickListener(this);
            button = (Button) rootView.findViewById(R.id.disclaimer_button);
            button.setOnClickListener(this);

            TextView textView = (TextView) rootView.findViewById(R.id.disclaimer_text);
            int page = getArguments().getInt(ARG_SECTION_NUMBER);
            if (page == 1)
            {
                textView.setText(getString(R.string.disclaimer_text_page_1));
            }
            else if (page == 2)
            {
                textView.setText(getString(R.string.disclaimer_text_page_2));
            }
            else if (page == 3)
            {
                textView.setText(getString(R.string.disclaimer_text_page_3));


                checkBox.setVisibility(CheckBox.VISIBLE);

                TextView linktext =(TextView)rootView.findViewById(R.id.disclaimer_terms_link);
                linktext.setMovementMethod(LinkMovementMethod.getInstance());
                String text = "<a href='http://vittal.com.ar/terminos-y-condiciones-idoc/'> " + getString(R.string.disclaimer_terms_link) + " </a>";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    linktext.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    linktext.setText(Html.fromHtml(text));
                }
                linktext.setVisibility(TextView.VISIBLE);
            }
            if(page != 3)
            {
                checkBox.setVisibility(CheckBox.INVISIBLE);
                button.setVisibility(Button.INVISIBLE);
            }
            return rootView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.disclaimer_checkbox:
                    if (checkBox.isChecked())
                    {
                        button.setVisibility(Button.VISIBLE);
                    }
                    else
                    {
                        button.setVisibility(Button.INVISIBLE);
                    }
                    break;
                case R.id.disclaimer_button:
                    SharedPreferences.Editor editor = shaPre.edit();
                    editor.putBoolean(LEGAL_KEY, Boolean.TRUE);
                    editor.apply();
                    Intent intent = new Intent(_activity, MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private DisclaimerActivity _activity;

        public SectionsPagerAdapter(FragmentManager fm, DisclaimerActivity activity) {
            super(fm);
            this._activity = activity;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, this._activity);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return NUM_PAGES;
        }
    }
}
