package com.jneko.ui.paginator;

import com.jneko.ui.controls.NumericTextField;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import jiconfont.javafx.IconNode;
//import org.slf4j.LoggerFactory;

public class Paginator extends HBox {
    //private final org.slf4j.Logger 
    //        logger = LoggerFactory.getLogger(Paginator.class);
        
    private int 
            currentPage = 1,
            pageCount = 0;
    
    private final Label 
            totalCount = new Label("0");
    
    private final HBox
            pagesContainer = new HBox(),
            navToContainer = new HBox();
    
    private final IconNode 
            iconPrev = new IconNode(),
            iconNext = new IconNode(),
            iconEnd = new IconNode(),
            iconStart = new IconNode(),
            iconNavTo = new IconNode();
    
    private final NumericTextField
            navToText = new NumericTextField("controlsGoodInput", "controlsBadInput");
    
    private final PaginatorActionListener
            actListener;
    
    public Paginator(PaginatorActionListener pl) {
        super();
        actListener = pl;
        this.getStyleClass().addAll("maxHeight", "paginatorSpacer");
        
        pagesContainer.setAlignment(Pos.CENTER);
        pagesContainer.getStyleClass().addAll("maxHeight", "maxWidth");
        
        navToContainer.setAlignment(Pos.CENTER);
        navToContainer.getStyleClass().addAll("maxHeight", "paginatorSpacer");
        
        iconPrev.getStyleClass().addAll("paginatorIcon", "paginatorIconPrev");
        iconNext.getStyleClass().addAll("paginatorIcon", "paginatorIconNext");
        iconStart.getStyleClass().addAll("paginatorIcon", "paginatorIconStart");
        iconEnd.getStyleClass().addAll("paginatorIcon", "paginatorIconEnd");
        iconNavTo.getStyleClass().addAll("paginatorIcon", "paginatorIconNavto");
        
        navToText.setAlignment(Pos.CENTER);
        navToText.getStyleClass().addAll("maxHeight");
        navToText.setText("1");
        
        iconPrev.setOnMouseClicked(c -> {
            pagPrev();
        });
        
        iconNext.setOnMouseClicked(c -> {
            pagNext();
        });
        
        iconStart.setOnMouseClicked(c -> {
            currentPage = 1;
            refresh();
        });
        
        iconEnd.setOnMouseClicked(c -> {
            currentPage = pageCount;
            refresh();
        });
        
        iconNavTo.setOnMouseClicked(c -> {
            if (!navToText.isValid()) return;
            currentPage = navToText.getInt();
            refresh();
        });
        
        navToText.setOnKeyPressed((KeyEvent key) -> {
            if (key.getCode() == KeyCode.ENTER) {
                if (!navToText.isValid()) return;
                currentPage = navToText.getInt();
                refresh();
            }
        });
        
        totalCount.setAlignment(Pos.CENTER);
        totalCount.getStyleClass().addAll("maxHeight", "paginatorTotalPagesInfo");
        totalCount.setMinWidth(Region.USE_PREF_SIZE);
        
        this.getChildren().addAll(
                pagesContainer,
                navToContainer
        );
        
        navToContainer.getChildren().addAll(
                navToText,
                iconNavTo
        );
        
        pagesContainer.getChildren().addAll(
                iconStart,
                iconPrev,
                totalCount,
                iconNext,
                iconEnd
        );
        
        refresh();
    }
    
    private void refresh() {
        totalCount.setText("Page " + currentPage + " of " + pageCount);
        if ((pageCount > 0) && (currentPage > 0)) actListener.OnPageChange(currentPage, pageCount);
    }
    
    public final void pagNext() {
        if (currentPage < pageCount) {
            currentPage++;
            refresh();
        }
    }
    
    public final void pagPrev() {
        if (currentPage > 1) {
            currentPage--;
            refresh();
        }
    }
    
    public final boolean isOnStart() {
        return (currentPage <= 1);
    }
    
    public final boolean isOnEnd() {
        return (currentPage >= pageCount);
    }
    
    public int getCurrentPageIndex() {
        return currentPage;
    }
    
    public void setCurrentPageIndex(int p) {
        if (pageCount > pageCount) return;
        currentPage = p;
        totalCount.setText("Page " + currentPage + " of " + pageCount);
//        refresh();
    }
    
    public void setPageCount(int p) {
        pageCount = p;
        totalCount.setText("Page " + currentPage + " of " + pageCount);
        navToText.setMinMax(1, pageCount); 
        //refresh();
    }
}
