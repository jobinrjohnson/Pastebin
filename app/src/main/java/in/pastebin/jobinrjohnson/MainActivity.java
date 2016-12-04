package in.pastebin.jobinrjohnson;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView mtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                startActivity(new Intent(MainActivity.this, AddPaste.class));

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        loadFrontProfile();

        mtv = (TextView) findViewById(R.id.mTV);

    }

    public void loadFrontProfile() {
        String url = getResources().getString(R.string.api_url) + "api_post.php";
        new ServerPaste().execute(url);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
            String modedData = data;
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

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, close;

            public MyViewHolder(View itemView) {
                super(itemView);

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
            data.put("api_results_limit", "100");
            return data;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                PastebinRequest request = new PastebinRequest(params[0]);
                request.postData(postData);
                if (request.resultOk()) {
                    dataReturned = request.getResponse();
                    status = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (type) {
                case 0:             //for trending posts
                    postData = getTrendPastePostData();
                    break;
                default:            //default hashmap
                    postData = new HashMap<>();
            }
        }


        private String getValue(String tag, Element element) {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (status) {
                //Toast.makeText(MainActivity.this, dataReturned, Toast.LENGTH_LONG).show();
                mtv.setText(dataReturned);

                String modedData = "<?xml version=\"1.0\"?>\n" +
                        "<records>" + dataReturned + "\t\n" +
                        "</records>";


                String rx = "";

                DocumentBuilderFactory factory;
                DocumentBuilder builder;
                factory = DocumentBuilderFactory.newInstance();
                try {
                    builder = factory.newDocumentBuilder();
                    StringReader sr = new StringReader(modedData);
                    InputSource is = new InputSource(sr);
                    Document d = builder.parse(is);

                    NodeList nList = d.getElementsByTagName("paste");

                    for (int i = 0; i < nList.getLength(); i++) {
                        Node node = nList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            rx += getValue("paste_key", element);
                        }
                    }

                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mtv.setText(rx);


            } else {
                Toast.makeText(MainActivity.this, "Nothing returned", Toast.LENGTH_LONG).show();
            }
        }
    }
}
