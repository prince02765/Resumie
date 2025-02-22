package com.example.resumie;


import android.content.ContentProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resumie.CV.CVFragment;
import com.example.resumie.PDF.PDFFragment;
import com.example.resumie.SharedPrefManager.SharedPrefManager;
import com.example.resumie.SideNavigation.MenuAdapter;
import com.example.resumie.SideNavigation.MenuItem;
import com.example.resumie.SideNavigation.MenuUtil;
import com.example.resumie.home.HomeFragment;
import com.example.resumie.portfolio.PortfolioFragment;
import com.example.resumie.team.TeamFragment;
import com.example.resumie.SideNavigation.ClickedCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ClickedCallback {

    ImageView imageView2;
    RecyclerView recyclerView;
    MenuAdapter menuAdapter;
    List<MenuItem> menu;
    int selectedMenuPosition=0;
    private int PICK_IMAGE = 13;

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSideNavigation();
        sharedPrefManager = new SharedPrefManager(this);

        setHomeFragment();


        if(sharedPrefManager.getHomeData()!=null && sharedPrefManager.getHomeData().getProfileImage()!=null)
        {
            Glide.with(this)
                    .load(sharedPrefManager.getHomeData().getProfileImage())
                    .centerCrop()
                    .into(imageView2);
        }
        else
        {
            Glide.with(this)
                    .load(R.drawable.user)
                    .centerCrop()
                    .into(imageView2);
        }
    }

    private void setSideNavigation() {
        recyclerView = findViewById(R.id.recyclerview_nav);
        imageView2 = findViewById(R.id.imageView2);

        menu= MenuUtil.getMenuList();
        menuAdapter=new MenuAdapter(menu,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(menuAdapter);

        onClickListeners();
    }

    private void onClickListeners() {

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popup = new PopupMenu(MainActivity.this, imageView2);
                popup.getMenu().add("Display Profile Photo");
                popup.getMenu().add("Upload new image");
                popup.getMenu().add("Cancel");




                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {

                        if(item.getTitle().equals("Upload new image"))
                        {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        }

                        else if(item.getTitle().equals("Cancel"))
                        {
                            popup.dismiss();
                        }
                        return false;
                    }
                });

                popup.show();



            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            String str = data.getData().toString();

            sharedPrefManager.setHomeData(str,6);

            Glide.with(this)
                    .load(str)
                    .centerCrop()
                    .into(imageView2);
        }
    }


    void setPortfolioFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new PortfolioFragment()).commit();
    }

    void setCVFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new CVFragment()).commit();
    }

    void setHomeFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
    }

    void setTeamFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new TeamFragment()).commit();
    }

    void setPDFFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new PDFFragment()).commit();
    }

    @Override
    public void onSideMenuItemClick(int i) {

        switch (menu.get(i).getCode()) {

            case MenuUtil.HOME_FRAGMENT : setHomeFragment();
                break;
            case MenuUtil.CV_FRAGMENT : setCVFragment();
                break;
            case MenuUtil.TEAM_FRAGMENT: setTeamFragment();
                break;
            case MenuUtil.PORTFOLIO_FRAGMENT: setPortfolioFragment();
                break;
            case MenuUtil.PDF_FRAGMENT: setPDFFragment();
                break;
            default: setHomeFragment();
        }

        menu.get(selectedMenuPosition).setSelected(false);
        menu.get(i).setSelected(true);
        selectedMenuPosition = i ;
        menuAdapter.notifyDataSetChanged();

    }
}