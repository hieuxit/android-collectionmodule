package io.fruitful.collectionmodule.sample;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.fruitful.collectionmodule.view.OnItemClickListener;
import io.fruitful.collectionmodule.view.RecyclerAdapter;
import io.fruitful.collectionmodule.view.SimplePagingRecyclerAdapter;
import io.fruitful.collectionmodule.viewmodel.PagingViewModel;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;
import rx.schedulers.Schedulers;

/**
 * Created by hieuxit on 9/27/16.
 */

public class ImagesViewModel extends PagingViewModel<List<String>, String> {
    static final String BASE = "http://i.imgur.com/";
    static final String EXT = ".jpg";
    static final String[] URLS = {
            BASE + "CqmBjo5" + EXT, BASE + "zkaAooq" + EXT, BASE + "0gqnEaY" + EXT,
            BASE + "9gbQ7YR" + EXT, BASE + "aFhEEby" + EXT, BASE + "0E2tgV7" + EXT,
            BASE + "P5JLfjk" + EXT, BASE + "nz67a4F" + EXT, BASE + "dFH34N5" + EXT,
            BASE + "FI49ftb" + EXT, BASE + "DvpvklR" + EXT, BASE + "DNKnbG8" + EXT,
            BASE + "yAdbrLp" + EXT, BASE + "55w5Km7" + EXT, BASE + "NIwNTMR" + EXT,
            BASE + "DAl0KB8" + EXT, BASE + "xZLIYFV" + EXT, BASE + "HvTyeh3" + EXT,
            BASE + "Ig9oHCM" + EXT, BASE + "7GUv9qa" + EXT, BASE + "i5vXmXp" + EXT,
            BASE + "glyvuXg" + EXT, BASE + "u6JF6JZ" + EXT, BASE + "ExwR7ap" + EXT,
            BASE + "Q54zMKT" + EXT, BASE + "9t6hLbm" + EXT, BASE + "F8n3Ic6" + EXT,
            BASE + "P5ZRSvT" + EXT, BASE + "jbemFzr" + EXT, BASE + "8B7haIK" + EXT,
            BASE + "aSeTYQr" + EXT, BASE + "OKvWoTh" + EXT, BASE + "zD3gT4Z" + EXT,
            BASE + "z77CaIt" + EXT,
    };

    public ImagesViewModel() {
        setResetWhenRefresh(true);
    }

    @Override
    protected RecyclerAdapter createAdapter() {
        SimplePagingRecyclerAdapter<String> adapter = new SimplePagingRecyclerAdapter<>(R.layout.item_image);
        adapter.setOnItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void onItemClick(View itemView, String data, int position) {

            }
        });
        return adapter;
    }

    Random random = new Random();
    int totalPage = 4;

    @Override
    protected Observable<List<String>> sourceOfPage(final int pageIndex) {
        return Observable.defer(new Func0<Observable<List<String>>>() {
            @Override
            public Observable<List<String>> call() {
                return Observable.timer(1, TimeUnit.SECONDS).map(new Func1<Long, List<String>>() {
                    @Override
                    public List<String> call(Long aLong) {
                        int rand = random.nextInt(5);
                        switch (rand) {
                            case 2:
                                throw new IllegalArgumentException("Crash for test");

                            case 0:
                            case 4:
                                return new ArrayList<>();
                            default:
                                int numberItemOfPage = URLS.length / totalPage;
                                ArrayList<String> images = new ArrayList<>();
                                for (int i = 0; i < numberItemOfPage; i++) {
                                    int index = pageIndex * numberItemOfPage + i;
                                    if (index >= URLS.length) break;
                                    images.add(URLS[index]);
                                }
                                return images;
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

//    @Override
//    protected Observable<List<String>> source() {
//        return Observable.defer(new Func0<Observable<List<String>>>() {
//            @Override
//            public Observable<List<String>> call() {
//                return Observable.timer(1, TimeUnit.SECONDS).map(new Func1<Long, List<String>>() {
//                    @Override
//                    public List<String> call(Long aLong) {
//                        int rand = random.nextInt(3);
//                        switch (rand) {
//                            case 1:
//                                return new ArrayList<String>();
//                            case 2:
//                                throw new IllegalArgumentException("Crash for test");
//                            default:
//                                return new ArrayList<>(Arrays.asList(URLS));
//                        }
//                    }
//                });
//            }
//        }).subscribeOn(Schedulers.io());
//    }

    @Override
    protected boolean noMorePages(List<String> response) {
        return response == null || response.size() == 0;
    }

    @Override
    protected Func1<List<String>, List<String>> transformer() {
        return UtilityFunctions.identity();
    }
}
