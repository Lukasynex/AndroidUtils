//to use insert in gradle scripts:
//compile 'com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:0.7.2'

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.ArrayList;

import pl.sointeractive.smartcity.R;
import pl.sointeractive.smartcity.config.Config;
import pl.sointeractive.smartcity.data.stops.StopNearby;
import pl.sointeractive.smartcity.data.stops.StopNearbyLine;
import pl.sointeractive.smartcity.fragments.MyJourneysFragment;
import pl.sointeractive.smartcity.objects.AbstractDataProvider;
import pl.sointeractive.smartcity.objects.ExampleDataProvider;
import pl.sointeractive.smartcity.objects.TripData;

/**
 * Created by Lukasz Marczak on 2015-07-13.
 */
public class DraggableSwipeableItemAdapter extends RecyclerView.Adapter<DraggableSwipeableItemAdapter.MyViewHolder>
        implements SwipeableItemAdapter<DraggableSwipeableItemAdapter.MyViewHolder> {

    private static final String TAG = DraggableSwipeableItemAdapter.class.getSimpleName();

    private static ArrayList<String> lineNumberDataset = new ArrayList<>();
    private static ArrayList<String> stopNameDataset = new ArrayList<>();
    private static ArrayList<String> busDirectionDataset = new ArrayList<>();
    private static ArrayList<String> colorDataset = new ArrayList<>();

    private static AlertDialog stopsDialog;
    private static DraggableSwipeableItemAdapter instance;

    public static AbstractDataProvider mProvider = null;


    //measuring first item
    protected int measuredDraggableListViewHeight = 0;
    protected int fixedHeaderViewHeight = 0;
    protected static View transparentView;
    protected boolean isHeaderViewFixed = false;
    protected View globalView;

    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;
    private static ViewGroup behindView = null;
    private Activity activity;

    protected MyViewHolder headerViewHolder;


    public interface EventListener {
        // void onItemRemoved(int position);

        void onItemPinnedRight(int position);

        void onItemPinnedLeft(int position);

        void onItemViewClicked(View v, boolean pinned);
    }


    public class MyViewHolder extends AbstractSwipeableItemViewHolder {
        public ViewGroup mContainer;

        public FrameLayout frame;
        public View mDragHandle;
        public TextView mTextView;
        public View v;
        public View redundantView = null;
        public int type;
        public TextView lineNumber, stopName, direction;
        public LinearLayout lineLayout;

        public MyViewHolder(View v, int viewType, ViewGroup _parent) {
            super(v);
            this.v = v;
            type = viewType;

            if (type != TRANSPARENT_VIEW) {

//                measureFirstItemHeight(_parent);

                frame = (FrameLayout) v.findViewById(R.id.frame_frame_frame);
                mContainer = (ViewGroup) v.findViewById(R.id.container);
                mDragHandle = v.findViewById(R.id.drag_handle);
//                mTextView = (TextView) v.findViewById(android.R.id.text1);
//                mTextView = (TextView) v.findViewById(android.R.id.draggable_list_item_stops_stop_name_data);
                mDragHandle.setVisibility(View.GONE);

                lineNumber = (TextView) v.findViewById(R.id.draggable_list_swipe_item_stops_bus_number_data);
                stopName = (TextView) v.findViewById(R.id.draggable_list_swipe_item_stops_stop_name_data);
                direction = (TextView) v.findViewById(R.id.draggable_swipe_list_item_stops_bus_direction);
                lineLayout = (LinearLayout) v.findViewById(R.id.draggable_list_swipe_item_stops_background);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int clickedItem = getPosition();
                        Log.d(TAG, "draggable list item " + clickedItem + " clicked");
                        //initLinesDialog(clickedItem);

                    }
                });


            } else {
                redundantView = v;
                v.setBackgroundColor(Color.TRANSPARENT);
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return behindView.dispatchTouchEvent(event);
                    }
                });
            }
        }

        @Override
        public View getSwipeableContainerView() {
            if (type != TRANSPARENT_VIEW)
                return mContainer;
            else
                return redundantView;
        }
    }

    public DraggableSwipeableItemAdapter(ViewGroup _behindView, Activity _activity) {
        this.activity = _activity;
//        mProvider = dataProvider;
        if (mProvider == null) {
            mProvider = new ExampleDataProvider().initWith(null);
        }
        behindView = _behindView;
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);

        updateStopsData();
    }

    public void updateDraggableList() {
        Log.d(TAG, "updateDraggableList()");
        updateStopsData();
        notifyDataSetChanged();
    }

    public static void updateStopsData() {
        Log.d(TAG, "updateStopsData()");

        //Check whether the List of nearby stops exists
        if (TripData.stopNearbyStopsList != null) {

            if (stopNameDataset != null)
                stopNameDataset.clear();
            if (busDirectionDataset != null)
                busDirectionDataset.clear();
            if (lineNumberDataset != null)
                lineNumberDataset.clear();
            if (colorDataset != null)
                colorDataset.clear();

            //Iterate through each received nearby stop
            for (StopNearby currentStop : TripData.stopNearbyStopsList) {

                //Check whether Line IDs exists
                if (currentStop.getRouteIds() != null) {

                    //iterate through each line
                    for (int lineDataElement = 0; lineDataElement < currentStop.getRouteIds().length; lineDataElement++) {

                        stopNameDataset.add(currentStop.getName());
                        String currentLineId = currentStop.getRouteIds()[lineDataElement];

                        for (StopNearbyLine lineData : TripData.stopNearbyLineDataList) {
                            if (lineData.isIdEquals(currentLineId)) {
                                busDirectionDataset.add(lineData.getLongName());
                                lineNumberDataset.add(lineData.getShortName());
                                colorDataset.add(lineData.getBACKGROUND_COLOR());
                            }
                        }
                    }
                }
            }
            mProvider = mProvider.initWith(lineNumberDataset, stopNameDataset, busDirectionDataset, colorDataset);
        } else {
            Log.d(TAG, "updating stops failed due to empty stopsNearbyList");
        }
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v, true); // true --- pinned
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v), false);  // false --- not pinned

        }
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TRANSPARENT_VIEW;
        return mProvider.getItem(position).getViewType();
    }

    public static final int TRANSPARENT_VIEW = -1;
    public static final int LIST_ITEM_1_VIEW = 0;
    public static final int LIST_ITEM_2_VIEW = 1;
    View defaultListItem = null;
    ViewGroup defaultParent = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View chosenView = null;
        switch (viewType) {
            case TRANSPARENT_VIEW: {
                globalView = null;
                if (Config.DENSITY.equals("HDPI")) {
                    globalView = inflater.inflate(R.layout.list_my_journeys_header_view_hdpi, parent, false);

                } else if (Config.DENSITY.equals("XHDPI")) {
                    globalView = inflater.inflate(R.layout.list_my_journeys_header_view_xhdpi, parent, false);
                } else if (Config.DENSITY.equals("XXHDPI")) {
                    globalView = inflater.inflate(R.layout.list_my_journeys_header_view_xxhdpi, parent, false);
                } else if (Config.DENSITY.equals("XXXHDPI")) {
                    globalView = inflater.inflate(R.layout.list_my_journeys_header_view_xxxhdpi, parent, false);
                } else {
                    globalView = inflater.inflate(R.layout.list_my_journeys_header_view, parent, false);
                }
                headerViewHolder = new MyViewHolder(globalView, viewType, parent);
                return headerViewHolder;
            }
            case LIST_ITEM_1_VIEW: {
//                chosenView = inflater.inflate(R.layout.list_item, parent, false);
                chosenView = inflater.inflate(R.layout.list_my_journeys_swipeable_item_view2, parent, false);
                defaultListItem = chosenView;
                defaultParent = parent;
                break;
            }
            case LIST_ITEM_2_VIEW: {
//                chosenView = inflater.inflate(R.layout.list_item2, parent, false);
//                chosenView = inflater.inflate(R.layout.list_my_journeys_swipeable_item_view2, parent, false);
                chosenView = inflater.inflate(R.layout.list_my_journeys_swipeable_item_view, parent, false);

                break;
            }
        }
        return new MyViewHolder(chosenView, viewType, parent);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            Log.d(TAG, "transparent Item");

        } else {

            position--;
            Log.d(TAG, "swipeable Item no " + position);

            final AbstractDataProvider.Data item = mProvider.getItem(position);

            // set listeners
            // (if the item is *not pinned*, click event comes to the itemView)
            holder.itemView.setOnClickListener(mItemViewOnClickListener);
            // (if the item is *pinned*, click event comes to the mContainer)
            holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);


//            holder.mTextView.setText(item.getText());
            holder.lineNumber.setText(item.getLineNumber());
            holder.stopName.setText(item.getStopName());
            holder.direction.setText(item.getDirection());

            try {
                holder.lineLayout.setBackgroundColor(Color.parseColor(item.getColor()));
            } catch (Exception e) {
                holder.lineLayout.setBackgroundColor(Color.parseColor("#996699"));
            }
//            try {
//                holder.lineLayout.setBackgroundColor(Color.parseColor("#" + colorDataset.get(position)));
//            } catch (Exception e) {
//                holder.lineLayout.setBackgroundColor(Color.parseColor("#996699"));
//            }
//            holder.mTextView.setText(item.getStopName());

            // set background resource (target view ID: container)
            final int swipeState = holder.getSwipeStateFlags();

            if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_UPDATED) != 0) {
                int bgResId;

                if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_ACTIVE) != 0) {
                    bgResId = R.drawable.bg_item_swiping_active_state;
                } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_SWIPING) != 0) {
                    bgResId = R.drawable.bg_item_swiping_state;
                } else {
                    bgResId = R.drawable.bg_item_normal_state;
                }
                //TODO:
                holder.mContainer.setBackgroundResource(bgResId);
            }

            // set swiping properties
            holder.setSwipeItemSlideAmount(
                    item.isPinnedToSwipeLeft() ? RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
        }
    }

    @Override
    public int getItemCount() {
        if (mProvider == null)
            return 0;
        return mProvider.getCount();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        return mProvider.getItem(position).getSwipeReactionType();
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        if (position == 0) {
            holder.redundantView.setBackgroundColor(Color.TRANSPARENT);
        } else {
//            int bgRes = 0;
//            switch (type) {
//                case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
//                    bgRes = R.drawable.bg_swipe_item_neutral;
//                    break;
//                case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
//                    bgRes = R.drawable.bg_swipe_item_left;
//                    break;
//                case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
//                    bgRes = R.drawable.bg_swipe_item_right;
//                    break;
//            }
            //holder.itemView.setBackgroundResource(bgRes);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int onSwipeItem(MyViewHolder holder, int position, int result) {
        Log.d(TAG, "onSwipeItem(position = " + position + ", result = " + result + ")");
        position--;
        switch (result) {
            // swipe right
            //---old code---
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                if (mProvider.getItem(position).isPinnedToSwipeLeft()) {
                    // pinned --- back to default position
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
                } else {
                    // not pinned --- remove
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
                }
                // swipe left -- pin
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION;
            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public void onPerformAfterSwipeReaction(MyViewHolder holder, int position, int result, int reaction) {
        Log.d(TAG, "onPerformAfterSwipeReaction(position = " + position + ", result = " + result + ", reaction = " + reaction + ")");
        position--;
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
//            mProvider.removeItem(position);
            Log.d(TAG, "detected swipe left, but not used");
            item.setPinnedToSwipeLeft(false); //jesli jest false, w lewo mozna 'iskac' item,
            notifyItemChanged(position);
            if (mEventListener != null) {
                mEventListener.onItemPinnedLeft(position);
            }
            item.switchBoolean();


        } else if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION) {

            Log.d(TAG, "detected swipe right, but not used");
            item.setPinnedToSwipeLeft(false); //jesli jest false, item moze byc tylko iskany
//            // jesli jest item.setPinnedToSwipeRight(true); oraz
//
//
            notifyItemChanged(position);
//
            if (mEventListener != null) {
                mEventListener.onItemPinnedRight(position);
            }
            item.switchBoolean();

        } else {
            item.setPinnedToSwipeLeft(false);
        }
        if (item.wasPinnedToSwipeLeft()) {
//            Toast.makeText(activity, "WasPinnedToSwipeLeft", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "item was pinned to swipe left");
            item.switchBoolean();
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }
}
