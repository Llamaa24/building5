package com.example.ujclasses;

import android.os.Bundle;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private BuildingAdapter buildingAdapter;
    private List<String> buildingDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("buildings");

        initializeData();

        buildingDetails = new ArrayList<>();
        buildingAdapter = new BuildingAdapter(buildingDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(buildingAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBuilding(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initializeData() {
        Building building11 = new Building("Building 11");
        building11.addFloor("Floor 1", createClasses("Class 1", "Class 2", "Class 3"));
        building11.addFloor("Floor 2", createClasses("Class 4", "Class 5", "Class 6"));
        building11.addFloor("Floor 3", createClasses("Class 7", "Class 8", "Class 9"));

        Building building17 = new Building("Building 17");
        building17.addFloor("Floor 1", createClasses("Class 10", "Class 11", "Class 12"));
        building17.addFloor("Floor 2", createClasses("Class 13", "Class 14", "Class 15"));
        building17.addFloor("Floor 3", createClasses("Class 16", "Class 17", "Class 18"));

        Building building5 = new Building("Building 5");
        // First Floor
        building5.addFloor("First Floor", createSections(
                new String[] { "Class 106", "Class 107", "Class 108", "Class 109" },  // Section A
                new String[] {},                                                     // Section B
                new String[] { "Class 103", "Class 104", "Class 105", "Class 106",
                        "Class 107", "Class 108", "Class 109", "Class 110",
                        "Class 111", "Class 112" },                           // Section C
                new String[] {}                                                      // Section D
        ));

        // Second Floor
        building5.addFloor("Second Floor", createSections(
                new String[] { "Class 203", "Class 204", "Class 205", "Class 206", "Class 207",
                        "Class 208", "Class 209", "Class 210", "Class 211", "Class 212" }, // Section A
                new String[] { "Class 203", "Class 204", "Class 205", "Class 206", "Class 207",
                        "Class 208", "Class 209", "Class 210", "Class 211", "Class 212" }, // Section B
                new String[] { "Class 203", "Class 204", "Class 205", "Class 206", "Class 207",
                        "Class 208", "Class 209", "Class 210", "Class 211", "Class 212" }, // Section C
                new String[] { "Class 203", "Class 204", "Class 205", "Class 206", "Class 207",
                        "Class 208", "Class 209", "Class 210", "Class 211", "Class 212" }  // Section D
        ));

        // Third Floor
        building5.addFloor("Third Floor", createSections(
                new String[] { "Class 303", "Class 304", "Class 305", "Class 306", "Class 307",
                        "Class 308", "Class 309", "Class 310", "Class 311", "Class 312" }, // Section A
                new String[] { "Class 303", "Class 304", "Class 305", "Class 306", "Class 307",
                        "Class 308", "Class 309", "Class 310", "Class 311", "Class 312" }, // Section B
                new String[] { "Class 303", "Class 304", "Class 305", "Class 306", "Class 307",
                        "Class 308", "Class 309", "Class 310", "Class 311", "Class 312" }, // Section C
                new String[] { "Class 303", "Class 304", "Class 305", "Class 306", "Class 307",
                        "Class 308", "Class 309", "Class 310", "Class 311", "Class 312" }  // Section D
        ));
        saveBuildingData(building11);
        saveBuildingData(building17);
        saveBuildingData(building5);
    }

    private Map<String, List<String>> createClasses(String... classes) {
        Map<String, List<String>> floor = new HashMap<>();
        List<String> classList = new ArrayList<>();
        for (String cls : classes) {
            classList.add(cls);
        }
        floor.put("Classes", classList);
        return floor;
    }

    private Map<String, List<String>> createSections(String[] sectionA, String[] sectionB, String[] sectionC, String[] sectionD) {
        Map<String, List<String>> sections = new HashMap<>();
        sections.put("Section A", Arrays.asList(sectionA));
        sections.put("Section B", Arrays.asList(sectionB));
        sections.put("Section C", Arrays.asList(sectionC));
        sections.put("Section D", Arrays.asList(sectionD));
        return sections;
    }

    private void saveBuildingData(Building building) {
        databaseReference.child(building.getName()).setValue(building);
    }

    private void searchBuilding(String buildingName) {
        databaseReference.child(buildingName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Building building = snapshot.getValue(Building.class);
                if (building != null) {
                    buildingDetails.clear();
                    for (Map.Entry<String, Map<String, List<String>>> floor : building.getFloors().entrySet()) {
                        buildingDetails.add(floor.getKey() + ":");
                        for (Map.Entry<String, List<String>> section : floor.getValue().entrySet()) {
                            buildingDetails.add("  " + section.getKey() + ": " + section.getValue());
                        }
                    }
                    buildingAdapter.notifyDataSetChanged();
                } else {
                    buildingDetails.clear();
                    buildingDetails.add(buildingName + " not found.");
                    buildingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                buildingDetails.clear();
                buildingDetails.add("Error: " + error.getMessage());
                buildingAdapter.notifyDataSetChanged();
            }
        });
    }
}