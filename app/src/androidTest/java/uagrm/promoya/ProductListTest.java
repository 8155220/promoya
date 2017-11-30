package uagrm.promoya;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uagrm.promoya.Model.Category;
import uagrm.promoya.Model.Store;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Shep on 11/30/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ProductListTest {


    /*@Rule
    public ActivityTestRule<ProductList> mProductListTestRule =
            new ActivityTestRule<>(ProductList.class,true,true); */ // el ultimo true para lanzar la actividad
    /*@Rule
    public ActivityTestRule<ProductList> mProductListTestRule =
            new ActivityTestRule<>(ProductList.class,true,false);*/
    @Rule
    public ActivityTestRule<ProductList> mProductListTestRule =
            new ActivityTestRule<ProductList>(ProductList.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent intent = new Intent(targetContext, ProductList.class);
                    Store store = new Store();
                    store.setBackgroundImgUrl("https://firebasestorage.googleapis.com/v0/b/uagrm420031149.appspot.com/o/images%2Fd7d354bb-b8f5-47f7-aa3f-1f10085be815?alt=media&token=9ddf8b3b-d605-4bcf-bb50-7db7d668badd");
                    store.setDescription("TestingLab");
                    store.setDisplayName("Expresso");
                    store.setLogoImgUrl("https://firebasestorage.googleapis.com/v0/b/uagrm420031149.appspot.com/o/images%2F9d17c508-715b-4118-bf57-b871438079ee?alt=media&token=96985eb6-c8e9-42eb-9692-0d7f73f3034a");
                    store.setStoreId("DrPFJffNZvfKOReOnZwZvP31srA3");
                    store.setLongitude(-63.1955168);
                    store.setLatitude(-17.776624);
                    store.setSubscriptionsCount(500);

                    Category model = new Category();
                    model.setCategoryId("DrPFJffNZvfKOReOnZwZvP31srA3");
                    model.setPrincipalCategory("Testing");
                    intent.putExtra("currentCategory",model);
                    intent.putExtra("STORE", store);

                    return intent;
                }
            };

    @Test
    public void checkIfRecyclerViewIsShown() throws Exception {
        // Click on the add note button


        onView(withId(R.id.recycler_product)).check(matches(isDisplayed()));
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_product)).perform(click());
        //intended(hasComponent(ProductDetail.class.getName()));
        //onView(withId(R.id.product_name)).check(matches(isDisplayed()));
       // onView(withId(R.id.recycler_product))
        //        .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        // Check if the add note screen is displayed
        //onView(withId(R.id.edtPrice)).check(matches(isDisplayed()));
    }
    @Test
    public void checkIfSearchBarIsShown() throws Exception {
        // Click on the add note button
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()));
    }
    @Test
    public void checkIfPerformClickOnRecycler() throws Exception {
        // Click on the add note button
        onView(withId(R.id.recycler_product)).perform(click());
    }

}
