package com.jneko.ui;

import com.jneko.hibernate.DirectoryField;

public interface MeWClickActionListener {
    public void OnClick(DirectoryField data, MeWSelectable element);
    public void OnDoubleClick(DirectoryField data, MeWSelectable element);
}
