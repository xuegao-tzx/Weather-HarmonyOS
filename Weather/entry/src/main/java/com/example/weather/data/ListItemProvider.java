package com.example.weather.data;

import com.example.weather.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.ArrayList;

/**
 * The type List item provider.
 */
public class ListItemProvider extends RecycleItemProvider {
    private AbilitySlice mSlice;
    private OnItemClickListener listener;
    private ArrayList<DataMo> dataMos = new ArrayList<>();

    /**
     * Instantiates a new List item provider.
     *
     * @param abilitySlice the ability slice
     * @param listener     the listener
     */
    public ListItemProvider(AbilitySlice abilitySlice, OnItemClickListener listener) {
        this.mSlice = abilitySlice;
        this.listener = listener;
    }

    /**
     * Set data.
     *
     * @param cityMos the city mos
     */
    public void setData(ArrayList<CityMo> cityMos) {
        this.dataMos.clear();
        int i = 0;
        ArrayList<CityMo> tempList = new ArrayList<>();
        for (CityMo mo : cityMos) {
            if (i == 3) {
                i = 0;
                dataMos.add(new DataMo(tempList));
                tempList = new ArrayList<>();
            }
            tempList.add(mo);
            i++;
        }
        dataMos.add(new DataMo(tempList));
        this.notifyDataChanged();
    }

    @Override
    public int getCount() {
        return dataMos.size();
    }

    @Override
    public Object getItem(int i) {
        return dataMos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        Component component_item = LayoutScatter.getInstance(mSlice).parse(ResourceTable.Layout_list_item, null, false);
        if (!(component_item instanceof ComponentContainer)) {
            return null;
        }
        ComponentContainer rootLayout = (ComponentContainer) component_item;
        DataMo dataMo = dataMos.get(i);
        for (CityMo mo : dataMo.cityMos) {
            Text titleItem = (Text) LayoutScatter.getInstance(mSlice).parse(ResourceTable.Layout_item_title, null, false);
            titleItem.setText(mo.cityName);
            rootLayout.addComponent(titleItem);
            titleItem.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    listener.OnItemClick(mo, i);
                }
            });
        }
        return component_item;
    }

    /**
     * The interface On item click listener.
     */
    public interface OnItemClickListener {
        /**
         * On item click.
         *
         * @param cityMo   the city mo
         * @param position the position
         */
        void OnItemClick(CityMo cityMo, int position);
    }
}

