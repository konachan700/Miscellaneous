package ru.mew_hpm.gpontools_v3.ui.adapters;

import ru.mew_hpm.gpontools_v3.dao.ONTInfo;

public interface ONTListAdapterActionListener {
    public void OnLongClick(ONTInfo item);
    public void OnClick(ONTInfo item);
}
