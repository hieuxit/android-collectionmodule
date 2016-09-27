package io.fruitful.collectionmodule.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import io.fruitful.collectionmodule.sample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImagesViewModel vm = new ImagesViewModel();
        vm.onViewModelCreate(savedInstanceState);
        binding.setVm(vm);

        vm.connect();
    }
}
