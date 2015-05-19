private void getDestinationFromSuggestion(int position) {
        if (mSuggestionsList.size() == 0 || (mSuggestionsList.size() < position))
            return;

        BusStop stop = mSuggestionsList.get(position);
        LatLng dest = new LatLng(stop.lat,stop.lon);
        TripData.toPlace = dest;
        search.setQuery(stop.busStopName, false);
        search.clearFocus();
    }

    public Cursor getSuggestionsCursor() {
        Log.d(TAG, " inside getSuggestionsCursor");
        // Load data from list to cursor
        String[] columns = new String[]{"_ID", "BUS_STOP_NAME"};
        Object[] temp = new Object[]{0, "default"};

        // Create a new Cursor object
        MatrixCursor cursor = new MatrixCursor(columns);


        int size = (mSuggestionsList.size() < TripRequestOptions.MAX_SUGGESTIONS_SEARCH) ? mSuggestionsList.size() : TripRequestOptions.MAX_SUGGESTIONS_SEARCH;
        for (int i = 0; i < size; i++) {

            temp[0] = i;
            temp[1] = mSuggestionsList.get(i).busStopName;

            // Add the  Google Place data as a row in the Cursor object
            cursor.addRow(temp);
        }

        Log.d(TAG, "suggestions size is: " + mSuggestionsList.size());

        return cursor;
    }

    /**
     * metoda wywołująca się za każdym razem gdy zmieni się tekst wpisywany w wyszukiwarce
     * @param input
     */
    public void autoComplete(String input) {

        Log.d(TAG, " inside autoComplete(" + input + ")");

        final String query = input;
        if (input.length() >= 3) {
            if (mThread != null) {
                mThread = null;
            }
            mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    //suggestionSet zawiera listę 'podpowiadanych' przystanków, które
                    final List<BusStop> suggestionsSet = Select.from(BusStop.class)
                            .where(Condition.prop("BUS_STOP_NAME").like("%"+query + "%"))
                            .list();



                    ArrayList<String> suggestedBusStopNames = new ArrayList<>();
                    for (BusStop s : suggestionsSet) {
                        suggestedBusStopNames.add(s.busStopName);
                    }
                    mSuggestionsList = suggestionsSet;
//                    TripData.lastBeforeDemoBusStop = mSuggestionsList.get(0);
                    final ArrayList<String> finalSet = suggestedBusStopNames;

                    // Update the SearchView's suggestions
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = getSuggestionsCursor();
                            search.setSuggestionsAdapter(new SearchSuggestionAdapter(getActivity(), cursor, finalSet));
                        }
                    });
                    Log.d(TAG, "suggestions list is: " + mSuggestionsList);
                }
            });
            mThread.start();
        } else if (input.length() < 3) {
            mSuggestionsList.clear();
            Cursor cursor = getSuggestionsCursor();
//            final ArrayList<String> finalSet = new ArrayList<>();
//            finalSet.clear();
            search.setSuggestionsAdapter(new SearchSuggestionAdapter(getActivity(), cursor, emptySet));
      }

    }
    private void initSearchView(View view1){
    final Intent intent = new Intent(context, SearchResultActivity.class);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        search = (SearchView) view1.findViewById(R.id.text_search_line);
        //dummy item for performance, spinner deleted(hidden)
        SearchView searchViewDummy =(SearchView) view1.findViewById(R.id.text_search_line_dummy);
        searchViewDummy.setInputType(InputType.TYPE_NULL);


        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        search.setQuery(TripData.lastBeforeDemoDestination, false);
        search.clearFocus();
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                Log.d(TAG, " inside onQueryTextSubmit (" + text + ")");
                intent.putExtra("QUERY", text);
                startActivityForResult(intent, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, " inside onQueryTextChange(" + newText + ")");

                autoComplete(newText);
                return true;
            }
        });
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                Log.d(TAG, " inside onSuggestionSelect (" + i + ")");
                getDestinationFromSuggestion(i);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Log.d(TAG, " inside onSuggestionClick (" + i + ")");
                getDestinationFromSuggestion(i);
                return true;
            }
        });
  }
