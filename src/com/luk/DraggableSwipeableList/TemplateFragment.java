 @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //noinspection ConstantConditions
        //mRecyclerView = (RecyclerView) getView().findViewById(R.id.draggable_list);
        draggableList = (RecyclerView) view.findViewById(R.id.draggable_list);

        mLayoutManager = new LinearLayoutManager(getActivity());

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        dragSwipeItemAdapter = new DraggableSwipeableItemAdapter(mapPlaceholder, getActivity());
        dragSwipeItemAdapter.setEventListener(new DraggableSwipeableItemAdapter.EventListener() {
            @Override
            public void onItemPinnedLeft(int position) {
                localSwipe = true;
                //Toast.makeText(getActivity(), "swipe Left item " + (position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemPinnedRight(int position) {
                localSwipe = false;
               // Toast.makeText(getActivity(), "swipe right item " + (position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                onItemViewClick(v, pinned);
            }
        });
        setupSwipeableAdapter();
    }
    private static boolean localSwipe;
    private void onItemViewClick(View v, boolean pinned) {
//        int position = mRecyclerView.getChildPosition(v);
        int position = draggableList.getChildPosition(v);
        if (position != RecyclerView.NO_POSITION) {
            String direction = "Mydlniki";
            if (localSwipe)
                direction = "Kombinat";

            Toast.makeText(getActivity(), "Direction: " + direction + " at item " + (position - 1) + ".", Toast.LENGTH_SHORT).show();
//            executeTrip();
        }
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }  private void setupSwipeableAdapter() {
        Log.d(TAG, "setupSwipeableAdapter");

        mAdapter = dragSwipeItemAdapter;
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(dragSwipeItemAdapter);      // wrap for swiping
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
//        mRecyclerView.setItemAnimator(animator);
        draggableList.setLayoutManager(mLayoutManager);
        draggableList.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        draggableList.setItemAnimator(animator);


        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            draggableList.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable)
                    getResources().getDrawable(R.drawable.material_shadow_z1_xhdpi)));

        }
        draggableList.addItemDecoration(new SimpleListDividerDecorator(getResources()
                .getDrawable(R.drawable.list_divider), true));
        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
//        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
//        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(draggableList);
        mRecyclerViewSwipeManager.attachRecyclerView(draggableList);


    }
