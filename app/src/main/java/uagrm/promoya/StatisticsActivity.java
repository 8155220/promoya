
package uagrm.promoya;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

//public class StatisticsActivity extends FragmentActivity {
public class StatisticsActivity extends AppCompatActivity {

    long today= System.currentTimeMillis();
    long month = 2592000000L;
    long lastmonth = today-month;
    long delta = month/cantDays;
    Toolbar toolbar;

    private CombinedChart mChart;
    public static final int cantDays=30;
    //
    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    protected String[] mMonths = new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
            , "13", "14", "15", "16", "17", "18", "19", "20", "21", "22"
            , "23", "24", "25", "26", "27", "28", "29", "30"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_statistics);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Estadisticas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");


        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }
        });

        CombinedData data = new CombinedData();

        //agregando Lineas
        data.setData(generateSubscriptionsLine());

        data.setValueTypeface(mTfLight);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();
    }
    private LineData generateSubscriptionsLine() {

        List<Entry> subscriptionData = new ArrayList<Entry>();
        List<Entry> likesData = new ArrayList<Entry>();
        List<Entry> viewsData = new ArrayList<Entry>();

        List<Integer> subscriptionDays = inicializarLista(500);
        List<Integer> likesDays = inicializarLista(1500);
        List<Integer> viewsDays = inicializarLista(3000);

        for (int i = 0; i < subscriptionDays.size(); i++) {
            Entry subs = new Entry(new Long(i), new Long(subscriptionDays.get(i)));
            Entry likes = new Entry(new Long(i), new Long(likesDays.get(i)));
            Entry views = new Entry(new Long(i), new Long(viewsDays.get(i)));
            subscriptionData.add(subs);
            likesData.add(likes);
            viewsData.add(views);
        }

        LineDataSet setComp1 = new LineDataSet(subscriptionData, "Suscripciones");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(getResources().getColor(R.color.suscriptionsLine));
        setComp1.setLineWidth(4.5f);
        setComp1.setCircleColor(getResources().getColor(R.color.suscriptionsLine));
        setComp1.setCircleRadius(5f);
        setComp1.setFillColor(getResources().getColor(R.color.suscriptionsLine));
        setComp1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setComp1.setDrawValues(true);
        setComp1.setValueTextSize(10f);
        setComp1.setValueTextColor(getResources().getColor(R.color.textColor));


        LineDataSet setComp2 = new LineDataSet(likesData, "Likes");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setColor(getResources().getColor(R.color.likesLine));
        setComp2.setLineWidth(4.5f);
        setComp2.setCircleColor(getResources().getColor(R.color.likesLine));
        setComp2.setCircleRadius(5f);
        setComp2.setFillColor(getResources().getColor(R.color.likesLine));
        setComp2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setComp2.setDrawValues(true);
        setComp2.setValueTextSize(10f);
        setComp2.setValueTextColor(getResources().getColor(R.color.textColor));

        LineDataSet setComp3 = new LineDataSet(viewsData, "Vistas");
        setComp3.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp3.setColor(getResources().getColor(R.color.viewsLine));
        setComp3.setLineWidth(4.5f);
        setComp3.setCircleColor(getResources().getColor(R.color.viewsLine));
        setComp3.setCircleRadius(5f);
        setComp3.setFillColor(getResources().getColor(R.color.viewsLine));
        setComp3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setComp3.setDrawValues(true);
        setComp3.setValueTextSize(10f);
        setComp3.setValueTextColor(getResources().getColor(R.color.textColor));


        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);
        dataSets.add(setComp3);

        LineData data = new LineData(dataSets);
        return data;
    }


    public List<Integer> inicializarLista(int cantidad){
        List<Long> listaLlaves = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            listaLlaves.add(randomLong(lastmonth,today));
        }
        return getListDay(listaLlaves);
    }

    public List<Integer> getListDay(List<Long> listaLlaves){
        long today= System.currentTimeMillis();
        long month = 2592000000L;
        long lastmonth = today-month;
        List<Integer> elegidos = new ArrayList<>();
        for (int i = 0; i < cantDays; i++) {
            elegidos.add(0);
        }

        for (int i = 0; i < listaLlaves.size(); i++) {
            if(listaLlaves.get(i)<today && listaLlaves.get(i)>lastmonth){
                if(whatDayItBelongs(listaLlaves.get(i))>=0)
                {
                    int dia = whatDayItBelongs(listaLlaves.get(i));
                    elegidos.set(dia,elegidos.get(dia)+1);
                }
            }
        }
        return elegidos;

    }

    public int whatDayItBelongs(long a){
        for (int i = 1; i <= cantDays; i++) {
            if(a <lastmonth+delta*i )
            {
                return i-1;
            }

        }
        return -1;
    }

    public long randomLong(){

        long LOWER_RANGE = lastmonth;
        long UPPER_RANGE = today;
        Random random = new Random();
        long randomValue =
                LOWER_RANGE +
                        (long)(random.nextDouble()*(UPPER_RANGE - LOWER_RANGE));
        return randomValue;
    }

    public static long randomLong(long min, long max) {

        long LOWER_RANGE = min;
        long UPPER_RANGE = max;
        Random random = new Random();
        long randomValue
                = LOWER_RANGE
                + (long) (random.nextDouble() * (UPPER_RANGE - LOWER_RANGE));
        return randomValue;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
