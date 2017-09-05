package ru.mew_hpm.gpontools_v3.ui.adapters;

import ru.mew_hpm.gpontools_v3.dao.SSHServerData;

public interface SSHServersListAdapterActionListener {
    public void OnMoreButtonClick(SSHServerData item);
    public void OnClick(SSHServerData item);
}
