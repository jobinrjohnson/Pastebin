package in.pastebin.jo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    View headerview;
    SharedPreferences sp;
    NavigationView navigationView;
    SwipeRefreshLayout srl;
    DisplayMetrics displaymetrics;

    boolean trends = true, adviewin = false;
    int iter, BREAK_POINT = 400;
    Button errButton;

    LinearLayoutManager lllayoutManager = new LinearLayoutManager(MainActivity.this);
    // StaggeredGridLayoutManager sgllayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        sp = getSharedPreferences("user", MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();

        if (sp.contains("user_key") && extras == null) {
            trends = false;
        }

        getSupportActionBar().setTitle((trends ? "Today's Trending" : "Your") + " Pastes");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddPaste.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        srl = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (trends) {
//                    srl.setRefreshing(false);
//                    return;
//                }
                loadFrontProfile();
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerview = navigationView.getHeaderView(0);

        errButton = (Button) findViewById(R.id.err_reload);
        errButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFrontProfile();
            }
        });

        loadFrontProfile();
        initVars();
        setupUserSettings();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

//        int width = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? displaymetrics.heightPixels : displaymetrics.widthPixels;
//
//        if (width / displaymetrics.density > BREAK_POINT) {
//            recyclerView.setLayoutManager(sgllayoutManager);
//        } else {
//            recyclerView.setLayoutManager(lllayoutManager);
//        }

    }

    void dudeChangedStatus(int status) {

        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        TextView tv = (TextView) findViewById(R.id.textView7);
        ImageView iv = (ImageView) findViewById(R.id.errimview);
        LinearLayout statDiv = (LinearLayout) findViewById(R.id.statDiv);

        switch (status) {
            case 0:
                pb.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
                tv.setText("An error occured. Unable to get data.");
                errButton.setVisibility(View.VISIBLE);
                break;
            case 1:
                statDiv.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            default:
                pb.setVisibility(View.VISIBLE);
                iv.setVisibility(View.GONE);
                errButton.setVisibility(View.GONE);
                tv.setText("Loading.");
        }

    }

    void setupUserSettings() {
        if (sp.contains("user_key")) {
            Picasso.with(MainActivity.this).load(sp.getString("user_avatar_url", "http://pastebin.com/i/guest.png")).into((ImageView) headerview.findViewById(R.id.myporfpic));
            TextView tvName, tvEmail;

            tvName = (TextView) headerview.findViewById(R.id.tvMyname);
            tvEmail = (TextView) headerview.findViewById(R.id.tvEmail);

            tvName.setText(sp.getString("user_name", "Unnamed User"));
            tvEmail.setText(sp.getString("user_email", "Unknown email"));

            MenuItem it = navigationView.getMenu().findItem(R.id.nav_logout);
            it.setVisible(true);
            it = navigationView.getMenu().findItem(R.id.nav_yourPastes);
            it.setVisible(true);
            it = navigationView.getMenu().findItem(R.id.nav_login);
            it.setVisible(false);
            it = navigationView.getMenu().findItem(R.id.nav_user);
            it.setVisible(true);


        } else {

        }
    }

    void initVars() {
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//        if (displaymetrics.widthPixels / displaymetrics.density > BREAK_POINT) {
//            recyclerView.setLayoutManager(sgllayoutManager);
//        } else {
        recyclerView.setLayoutManager(lllayoutManager);
//        }

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.contains("user_key")) {
                    startActivity(new Intent(MainActivity.this, UserHome.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
    }

    public void loadFrontProfile() {
        String url = getResources().getString(R.string.api_url) + "api_post.php";
        new ServerPaste(trends ? 0 : 1).execute(url);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_yourPastes:
                if (trends) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.nav_trending:
                if (!trends) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("trending", true);
                    startActivity(intent);
                }
                break;
            case R.id.nav_add_paste:
                startActivity(new Intent(MainActivity.this, AddPaste.class));
                break;
            case R.id.nav_user:
                startActivity(new Intent(MainActivity.this, UserHome.class));
                break;
            case R.id.nav_logout:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure to logout")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor ediit = sp.edit();
                                ediit.clear();
                                ediit.commit();

                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .show();

                break;
            case R.id.nav_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.nav_manage:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class PastesAdapter extends RecyclerView.Adapter<PastesAdapter.MyViewHolder> {

        DocumentBuilderFactory factory;
        DocumentBuilder builder;

        NodeList nList;

        public PastesAdapter(String data) {
            super();

            String modedData = "<records>" + data + "</records>";
            factory = DocumentBuilderFactory.newInstance();
            try {
                builder = factory.newDocumentBuilder();
                StringReader sr = new StringReader(modedData);
                InputSource is = new InputSource(sr);
                Document d = builder.parse(is);
                nList = d.getElementsByTagName("paste");

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private String getValue(String tag, Element element) {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }

        @Override
        public int getItemCount() {
            return nList.getLength();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public String getPasteKey(int position) {
            Node node = nList.item(position);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                return getValue("paste_key", element);
            }
            return "";
        }

        public String getPasteUrl(int position) {
            Node node = nList.item(position);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                return getValue("paste_url", element);
            }
            return "";
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_home_pastes, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            Node node = nList.item(position);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                try {
                    final Element element = (Element) node;
                    holder.paste_format_long.setText(getValue("paste_format_long", element));
                    holder.paste_size.setText(getValue("paste_size", element) + "B");
                    holder.paste_hits.setText(getValue("paste_hits", element) + " Hits");
                    if (getValue("paste_expire_date", element).equals("0")) {
                        holder.paste_expire.setText("No expiry date set");
                    } else {
                        Timestamp stamp = new Timestamp(Integer.parseInt(getValue("paste_expire_date", element)));
                        Date date = new Date(stamp.getTime());
                        holder.paste_expire.setText("Expire on " + date);
                    }

                    if (getValue("paste_private", element).equals("0")) {
                        holder.private_ind.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_open));
                    } else if (getValue("paste_private", element).equals("1")) {
                        holder.private_ind.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlisted));
                    } else {
                        holder.private_ind.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock));
                    }


                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(MainActivity.this, ViewPaste.class);
                            i.putExtra("paste_id", getValue("paste_key", element));
                            try {
                                i.putExtra("paste_name", getValue("paste_title", element));
                            } catch (Exception e) {
                                i.putExtra("paste_name", "View Paste");
                            }
                            if (!trends) {
                                i.putExtra("mine", true);
                            }
                            startActivity(i);
                        }
                    });

                    holder.paste_title.setText(getValue("paste_title", element));

                    if (position % 4 == 0 && holder.adcontainer.getChildCount() < 1) {
                        NativeExpressAdView mNativeExpressAdView;
                        mNativeExpressAdView = new NativeExpressAdView(MainActivity.this);
                        mNativeExpressAdView.setLayoutParams(new NativeExpressAdView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        mNativeExpressAdView.setAdSize(new AdSize(AdSize.FULL_WIDTH, 132));
                        mNativeExpressAdView.setAdUnitId(getResources().getString(R.string.admob_adview_cus_1));
                        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

                        //adRequestBuilder.addTestDevice("28776EC697A5120CBA87CB573E26544A");

                        holder.adcontainer.addView(mNativeExpressAdView);
                        mNativeExpressAdView.loadAd(adRequestBuilder.build());
                        adviewin = true;
                    }

                } catch (Exception e) {
                    holder.paste_title.setText("No title.");
                }
            }

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView paste_title, paste_format_long, paste_size, paste_hits, paste_expire;
            LinearLayout container, adcontainer;
            ImageView private_ind;

            public MyViewHolder(View itemView) {
                super(itemView);
                paste_title = (TextView) itemView.findViewById(R.id.paste_title);
                paste_format_long = (TextView) itemView.findViewById(R.id.paste_format_long);
                paste_size = (TextView) itemView.findViewById(R.id.paste_size);
                paste_hits = (TextView) itemView.findViewById(R.id.paste_hits);
                paste_expire = (TextView) itemView.findViewById(R.id.paste_expire);
                container = (LinearLayout) itemView.findViewById(R.id.llcontainer);
                adcontainer = (LinearLayout) itemView.findViewById(R.id.adcontainer);
                private_ind = (ImageView) itemView.findViewById(R.id.private_ind);
            }
        }
    }


    private class ServerPaste extends AsyncTask<String, Void, String> {

        HashMap<String, String> postData;
        String dataReturned;
        boolean status = false;
        int type;

        ServerPaste(int type) {
            this.type = type;
        }

        ServerPaste() {
            type = 0;
        }

        public HashMap<String, String> getTrendPastePostData() {
            HashMap<String, String> data = new HashMap<>();
            data.put("api_option", "trends");
            data.put("api_dev_key", getResources().getString(R.string.api_key));
            return data;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                PastebinRequest request = new PastebinRequest(params[0], MainActivity.this);
                request.postData(postData);
                if (request.resultOk()) {
                    dataReturned = request.getResponse();
                    status = true;
                    ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir(), "") + (type == 1 ? "cachefile1.txt" : "cachefile2.txt")));
                    out.writeObject(dataReturned.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        public HashMap<String, String> getUserPastePostData() {
            HashMap<String, String> data = new HashMap<>();
            data.put("api_option", "list");
            data.put("api_user_key", sp.getString("user_key", ""));
            data.put("api_results_limit", "999");
            data.put("api_dev_key", getResources().getString(R.string.api_key));
            return data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dudeChangedStatus(2);
            switch (type) {
                case 0:             //for trending posts
                    postData = getTrendPastePostData();
                    break;
                case 1:             //for user posts
                    postData = getUserPastePostData();
                    break;
                default:            //default hashmap
                    postData = new HashMap<>();
            }
        }


        String readoffline() throws IOException {
            String fileContent = "";
            String currentLine;
            BufferedReader bufferedReader;
            FileInputStream fileInputStream = new FileInputStream(getFilesDir() + (type == 1 ? "cachefile1.txt" : "cachefile2.txt"));
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            while ((currentLine = bufferedReader.readLine()) != null) {
                fileContent += currentLine + '\n';
            }
            bufferedReader.close();
            return fileContent;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (srl.isRefreshing()) {
                srl.setRefreshing(false);
            }
            if (status) {
                PastesAdapter pastesAdapter = new PastesAdapter(dataReturned);
                recyclerView.setAdapter(pastesAdapter);
                dudeChangedStatus(1);
            } else {
                try {
                    String tempresult = readoffline();
                    PastesAdapter pastesAdapter = new PastesAdapter(tempresult);
                    recyclerView.setAdapter(pastesAdapter);
                    dudeChangedStatus(1);
                    Toast.makeText(MainActivity.this, "Served from offline", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    dudeChangedStatus(0);
                    e.printStackTrace();
                }
            }
        }
    }
}
