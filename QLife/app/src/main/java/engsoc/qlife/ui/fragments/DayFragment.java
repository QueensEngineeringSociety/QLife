package engsoc.qlife.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import engsoc.qlife.R;
import engsoc.qlife.activities.MainTabActivity;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.interfaces.OnHomePressedListener;
import engsoc.qlife.utility.HomeButtonListener;
import engsoc.qlife.utility.Util;
import engsoc.qlife.ui.recyclerview.DataObject;
import engsoc.qlife.ui.recyclerview.RecyclerViewAdapter;
import engsoc.qlife.database.local.courses.OneClass.OneClass;
import engsoc.qlife.database.local.courses.OneClass.OneClassManager;
import engsoc.qlife.interfaces.IQLActionbarFragment;
import engsoc.qlife.interfaces.IQLDrawerItem;
import engsoc.qlife.interfaces.IQLListFragmentWithChild;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fragment that displays the classes for a given day. When a class is clicked, it starts
 * EventInfoFragment that provides details about the class.
 */
public class DayFragment extends Fragment implements IQLActionbarFragment, IQLDrawerItem, IQLListFragmentWithChild {

    public static final String TAG_TITLE = "event_title";
    public static final String TAG_DATE = "date";
    public static final String TAG_LOC = "event_locat";

    private static int mInstances = 0;
    private static SparseIntArray mArray = new SparseIntArray();
    private int mTotalDaysChange = 0;
    private HomeButtonListener mHomeListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private View mView;
    private TextView mDateText;
    private String mDateString;
    private Calendar mCalendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_day, container, false);
        setActionbarTitle();

        mDateText = (TextView) mView.findViewById(R.id.date);
        mCalendar = Calendar.getInstance();
        inflateListView();
        onListItemChosen(null); //this is special case that doesn't need view - RecyclerView not ListView here

        mHomeListener = new HomeButtonListener(getContext());
        mHomeListener.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                mArray.put(mInstances, 0);
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeListener.startListening();

        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstances++;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!((MainTabActivity) getActivity()).isToActivity()) {
            mArray.put(mInstances, mTotalDaysChange); //save number of days to move day view
        } else {
            mArray.put(mInstances, 0);
        }
        deselectDrawer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mArray.delete(mInstances); //instance gone, don't need entry
        mInstances--;
        mHomeListener.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
        changeDate(mArray.get(mInstances, 0)); //account for day changed before moved fragments
    }

    public void changeDate(int numChange) {
        mCalendar.add(Calendar.DAY_OF_YEAR, numChange);
        mAdapter = new RecyclerViewAdapter(getDayEventData(mCalendar));
        mRecyclerView.setAdapter(mAdapter);
    }

    public ArrayList<DataObject> getDayEventData(Calendar calendar) {
        TextView noClassMessage = (TextView) mView.findViewById(R.id.no_class_message);
        noClassMessage.setVisibility(View.GONE); //updates day view when go to new day - may have class
        OneClassManager oneClassManager = new OneClassManager(this.getContext());

        List<String> list = new ArrayList<String>();
        List<String> loc = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<String> des = new ArrayList<>();
        ArrayList<DataObject> result = new ArrayList<DataObject>();

        ArrayList<DatabaseRow> data = oneClassManager.getTable();

        int day, month, year;
        boolean eventsToday = false;

        int calDay = calendar.get(Calendar.DAY_OF_MONTH);
        int calMon = calendar.get(Calendar.MONTH) + 1;
        int calYear = calendar.get(Calendar.YEAR);

        CharSequence f = DateFormat.format("yyyy-MM-dd", calendar.getTime());
        CharSequence date = DateFormat.format("EEEE, MMMM d, yyyy", mCalendar.getTime());
        mDateString = date.toString();
        mDateText.setText(date);

        //look for selected day of event in database
        for (int i = 0; i < data.size(); i++) {
            OneClass oneClass = (OneClass) data.get(i);
            day = Integer.parseInt(oneClass.getDay());
            month = Integer.parseInt(oneClass.getMonth());
            year = Integer.parseInt(oneClass.getYear());


            if (year == calYear && month == calMon && calDay == day) { // if the day matches add the event
                list.add(oneClass.getType());
                loc.add(oneClass.getRoomNum());
                time.add(oneClass.getStartTime() + "-" + oneClass.getEndTime());
                des.add(oneClass.getType());

                eventsToday = true;
            }
        }

        if (!eventsToday) {
            noClassMessage.setVisibility(View.VISIBLE);
            return result;
        }

        int startHour;
        int startMin;
        int posSmall = 0;
        int minHour;
        int minMin;
        int endHour = 0;
        int endMin = 0;
        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) == ("Nothing is happening today")) {
                result.add(new DataObject(list.get(i), f.toString()));
                list.remove(i);
                i -= 1;

            } else {
                minHour = 25;
                minMin = 61;

                for (int j = 0; j < list.size(); j++) {
                    String s = time.get(j);
                    String s1 = s.substring(0, s.indexOf("-"));
                    int div = s1.indexOf(":");
                    String shour = s1.substring(0, div);
                    String smin = s1.substring(div + 1, s1.length());

                    int index = s.indexOf("-") + 1;
                    String s2 = s.substring(index, s.length());
                    div = s2.indexOf(":");

                    startHour = Integer.parseInt(shour);
                    startMin = Integer.parseInt(smin);
                    if (startHour < minHour) {
                        posSmall = j;
                        minHour = startHour;
                        minMin = startMin;
                        endHour = Integer.parseInt(s2.substring(0, div));
                        endMin = Integer.parseInt(s2.substring(div + 1, s2.length()));

                    } else if (startHour == minHour) {
                        if (startMin < minMin) {
                            posSmall = j;
                            minHour = startHour;
                            minMin = startMin;
                            endHour = Integer.parseInt(s2.substring(0, div));
                            endMin = Integer.parseInt(s2.substring(div + 1, s2.length()));

                        }
                    }
                }
                String amPMTime;
                if (minHour > 12)
                    amPMTime = (minHour - 12) + ":" + minMin + "-" + (endHour - 12) + ":" + endMin + " PM";
                else if (endHour > 12)
                    amPMTime = (minHour) + ":" + minMin + "-" + (endHour - 12) + ":" + endMin + " PM";
                else amPMTime = time.get(posSmall) + " AM";

                result.add(new DataObject(list.get(posSmall), amPMTime + " at: " + loc.get(posSmall)));
                list.remove(posSmall);
                time.remove(posSmall);
                loc.remove(posSmall);
                des.remove(posSmall);
                i = -1;
            }
        }
        if (list.size() > 0) {
            result.add(new DataObject(list.get(0), time.get(0) + " at: " + loc.get(0) + " description: " + des.get(0)));
        }
        return result;
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_day), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_day, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_day, true);
    }

    @Override
    public void inflateListView() {
        if (getArguments() != null && getArguments().getString(MonthFragment.TAG_FROM_MONTH, "").equals(MonthFragment.TAG_FROM_MONTH)) {
            mCalendar.set(Calendar.DAY_OF_MONTH, getArguments().getInt(MonthFragment.TAG_DAY));
            mCalendar.set(Calendar.MONTH, getArguments().getInt(MonthFragment.TAG_MONTH));
            mCalendar.set(Calendar.YEAR, getArguments().getInt(MonthFragment.TAG_YEAR));
        }

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(getDayEventData(mCalendar));
        mRecyclerView.setAdapter(mAdapter);

        Button nextButton = (Button) mView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(1);
                mTotalDaysChange += 1;
            }
        });
        Button prevButton = (Button) mView.findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(-1);
                mTotalDaysChange += -1;
            }
        });
    }

    @Override
    public void onListItemChosen(View view) {
        ((RecyclerViewAdapter) mAdapter).setOnItemClickListener(new RecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                DataObject data = ((RecyclerViewAdapter) mAdapter).getItem(position);

                CardView card = (CardView) mView.findViewById(R.id.card_view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    card.setTransitionName("transistion_event_info" + position);
                }

                String cardName = card.getTransitionName();
                EventInfoFragment nextFrag = new EventInfoFragment();

                Bundle bundle = new Bundle();
                bundle.putString(TAG_TITLE, data.getmText1());
                bundle.putString(TAG_LOC, data.getmText2());
                bundle.putString(TAG_DATE, mDateString);
                nextFrag.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .replace(R.id.content_frame, nextFrag)
                        .addSharedElement(card, cardName)
                        .commit();
            }
        });
    }

    @Override
    public Bundle setDataForOneItem(View view) {
        return null; //RecyclerView special case, others ListFragment
    }
}