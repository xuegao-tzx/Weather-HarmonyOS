package org.devio.hm.weatherapp.slice;

import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import org.devio.hi.json.HiJson;
import org.devio.hm.weatherapp.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import org.devio.hm.weatherapp.data.CityMo;
import org.devio.hm.weatherapp.data.ListItemProvider;
import org.devio.hm.weatherapp.net.HiNet;
import org.devio.hm.weatherapp.net.IHiNet;
import org.devio.hm.weatherapp.util.HiExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainAbilitySlice extends AbilitySlice implements ListItemProvider.OnItemClickListener{
    private DirectionalLayout mLayout = new DirectionalLayout(this);
    private ListContainer listContainer;
    private ListItemProvider listItemProvider;
    private ArrayList<CityMo> cities = new ArrayList<>();
    private HiNet hiNet;
    private Text temperatureText,weatherText,tipsText;
    private Image weatherImage;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        hiNet = new HiNet();
        initLayout();
        intCity();
        loadData(new CityMo("北京", "110000"));
    }

    private void intCity() {
        cities.add(new CityMo("北京", "110000"));
        cities.add(new CityMo("上海", "310000"));
        cities.add(new CityMo("广州", "440100"));
        cities.add(new CityMo("深圳", "440300"));
        cities.add(new CityMo("重庆", "500000"));
        cities.add(new CityMo("杭州", "330100"));
        cities.add(new CityMo("成都", "510100"));
        cities.add(new CityMo("武汉", "420100"));
        cities.add(new CityMo("南京", "320100"));
        listItemProvider.setData(cities);
    }


    private void initLayout(){
        listContainer = (ListContainer)findComponentById(ResourceTable.Id_list);
        listItemProvider = new ListItemProvider(this, this);
        listContainer.setItemProvider(listItemProvider);
        temperatureText = (Text)findComponentById(ResourceTable.Id_temperature);
        weatherText = (Text)findComponentById(ResourceTable.Id_weather);
        tipsText = (Text)findComponentById(ResourceTable.Id_tips);
        weatherImage = (Image) findComponentById(ResourceTable.Id_weather_icon);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void OnItemClick(CityMo cityMo, int position) {

        loadData(cityMo);
    }

    private void loadData(CityMo mo){
        //https://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=<用户key>
        Map<String, String> params = new HashMap<>();
        params.put("city", mo.cityCode);
        params.put("key", "009da21206d7296cc58a2644e4e13bd0");
        hiNet.get("https://restapi.amap.com/v3/weather/weatherInfo", params, new IHiNet.NetListener() {
            @Override
            public void onSuccess(HiJson res) {
                HiLog.info(new HiLogLabel(HiLog.LOG_APP,  0x00201, "WeatherApp"), "Get success:" + res.toString());
                System.out.println("WeatherApp onSuccess:"+ res);
                bindData(res, mo);
            }


            @Override
            public void onFail(String message) {
                HiLog.info(new HiLogLabel(HiLog.LOG_APP,  0x00201, "WeatherApp"), "Get fail:" + message);
                System.out.println("WeatherApp onSuccess:"+ message);
            }
        });
    }

    private void bindData(HiJson res, CityMo mo) {
        HiJson hiJson = res.get("lives").get(0);
        String province = hiJson.value("province");
        String city = hiJson.value("city");
        String temperature = hiJson.value("temperature");
        String weather = hiJson.value("weather");
        String winddirection = hiJson.value("winddirection");
        String windpower = hiJson.value("windpower");
        String humidity = hiJson.value("humidity");

        temperatureText.setText(temperature + "℃");
        weatherText.setText(weather);
        tipsText.setText(mo.cityName + ":" + winddirection + "风" + windpower + " 空气湿度" + humidity + "%");
        int id = ResourceTable.Media_sunshine;
        if (weather.contains("阴")){
            id = ResourceTable.Media_overcast;
        } else if (weather.contains("雨")){
            id = ResourceTable.Media_rain;
        } else if (weather.contains("雪")){
            id = ResourceTable.Media_snow;
        } else if (weather.contains("雷电")){
            id = ResourceTable.Media_thunder;
        } else if (weather.contains("多云")){
            id = ResourceTable.Media_cloudy;
        }
        weatherImage.setImageAndDecodeBounds(id);
    }
}
