package com.example.basket;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AppCompatActivity {

    private List<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private ListView listView_cart;
    private Button btnRemove;
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<String> arraylist;
    private ArrayList<Integer> id_list;
    private Map<String, Integer> map=new HashMap<String, Integer>();
    private ArrayList<ListViewAdapterData> cart_arraylist=new ArrayList<ListViewAdapterData>();
    private String removingItem="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSearch = (EditText) findViewById(R.id.editSearch);
        listView = (ListView) findViewById(R.id.listView);
        listView_cart=(ListView) findViewById(R.id.cart_list);
        btnRemove=(Button)findViewById(R.id.button2);

        // 리스트를 생성한다.
        list = new ArrayList<String>(Arrays.asList("해태포키블루베리41G", "농심오징어집83G", "농심매운새우깡90G", "크라운)콘초66G", "농심바나나킥75G", "롯데)드림카카오82_(GABA)86G", "롯데ABC밀크65G", "롯데야채크래커249G", "CJ쁘띠첼워터 젤리오렌지130ML", "머거본)알땅콩", "해태에이스121G", "해태)허니버터칩38G", "해태)구운대파70G", "농심알새우칩68G", "트롤리키스100G", "농심수미칩오리지널85G", "오뚜기컵누들매콤37.8G", "오뚜기컵누들우동38.1G", "농심생생우동면276G", "오뚜기진라면순한맛120G", "삼양)삼양라면120G(봉지)", "농심신라면120G", "농심안성탕면125G", "농심사리곰탕큰사발111G", "농심새우탕큰사발115G", "삼양큰컵불닭볶음면105G", "농심올리브짜파게티140G", "팔도비빔면", "오뚜기참깨라면용기110G", " 오뚜기진짬뽕(큰컵)115G", "웅진아침햇살500ML", "코카콜라제로250ML", "동아포카리스웨트500ML", "롯데밀키스500ML", "광동)옥수수수염차500ML", "코카콜라1.5L", "코카환타오렌지1.5L", "코카환타파인애플1.5L", "롯데밀키스1.5L", "롯데)레쓰비190ml", "코카콜라제로500ML", "코카토레타500ML", " 제주삼다수500ML"));
        id_list=new ArrayList<Integer>(Arrays.asList(10060, 10092, 10093, 10094, 10095, 10110, 10175, 15044, 15731, 20121, 20123, 20164, 20166, 20171, 20219, 25617, 10111, 20111, 25055, 30103, 30124, 30125, 30126, 60091, 60108, 60120, 70133, 70136, 90128, 90136, 10005, 10034, 10037, 10038, 10043, 10075, 10076, 10077, 10078, 20012, 20031, 20033, 20155));
        for (int i=0; i<id_list.size(); i++){
            map.put(list.get(i), id_list.get(i));
        }

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.
        arraylist = new ArrayList<String>();
        arraylist.addAll(list);

        // 리스트에 연동될 아답터를 생성한다.
        adapter = new SearchAdapter(list, this);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);

        //display cart list

        displayList();
        btnRemove.setOnClickListener(new Button.OnClickListener() {         //선택한 제품 삭제
            @Override
            public void onClick(View view) {
                deleteProduct(removingItem);
                Toast.makeText(MainActivity.this, removingItem+"(이)가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                displayList();
            }
        });
        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Toast.makeText(MainActivity.this, list.get(position)+"(이)가 추가되었습니다.", Toast.LENGTH_SHORT).show();
               insertProduct(list.get(position));
               displayList();
            }
        });

        listView_cart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removingItem=cart_arraylist.get(position).getName();
            }
        });
    }

    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arraylist.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(arraylist.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    public void displayList(){
        cart_arraylist.clear();
        DbHelper helper=new DbHelper(this);
        SQLiteDatabase database=helper.getReadableDatabase();
        Cursor cursor=database.rawQuery("SELECT * FROM cart1", null);
        ListViewAdapter adapter=new ListViewAdapter();
        while (cursor.moveToNext()){
            adapter.addItemToList(cursor.getInt(0),cursor.getString(1));
        }
        listView_cart.setAdapter(adapter);
        cart_arraylist.addAll(adapter.list);
    }

    public void insertProduct(String n){
        DbHelper helper = new DbHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        Integer i=map.get(n);
        String qry = "INSERT INTO cart1(_id,name) VALUES('"+i+"', '"+n+"')";
        database.execSQL(qry);
        displayList();
    }

    public boolean deleteProduct(String n){
        DbHelper helper = new DbHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        Integer i=map.get(n);
        return database.delete("cart1", "_id=" + i, null)>0;
    }


}