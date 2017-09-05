package xgraphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Date;

public class XDisplay extends javax.swing.JPanel {
    private final Color 
            lines_color = new Color(250, 250, 250, 88), 
            lines_color2 = new Color(170, 170, 170), 
            tx_color = new Color(150, 50, 50), 
            rx_color = new Color(50, 150, 50);
    private long curDate = 0;
    
    private long[] DeltaArrayTx = null, DeltaArrayRx = null;
    public long daMaxTx=0, daMinTx=0, daMaxRx=0, daMinRx=0, deltaAvg=0, deltaAvgRx=0, deltaAvgTx=0;
    
    private final float 
            DIV2 = 2.15f;
    private final int 
            FONTSIZE = 10, 
            SPACER = 2;
    private boolean 
            TextInfo = true;
    private final Font  
            text_font       = new Font("Liberation Sans", 1, FONTSIZE),
            big_text_font   = new Font("Liberation Sans", 1, 96);
    
    private void __paintLines(Graphics g) {
        g.setColor(lines_color2); 
        g.setFont(text_font);
        final int 
                h = this.getHeight(), 
                h10 = this.getHeight() / 10, 
                w = this.getWidth(),
                dMaxTXMb = Math.round(((daMaxTx * 8) / (1024f * 1024f))),
                dMaxRXMb = Math.round(((daMaxRx * 8) / (1024f * 1024f)));
 
        final int y1 = Math.round(h/DIV2), y2 = h-Math.round(h/DIV2);
        
//        g.drawLine(60, y1, w, y1);
//        g.drawLine(60, y2, w, y2);
        
        //g.drawLine(40, 2, 40, h-4);
        float yn = y1; yn = yn / 5;  //((daMaxRx / (1024f * 1024f * 8)));
        for (float i = yn; i <= y1; i = i + yn) {
            g.drawLine(2, Math.round(i), 50, Math.round(i));
            g.drawLine(2, h - Math.round(i), 50, h - Math.round(i));
            g.drawString(Math.round((dMaxTXMb / (y1 / i))) + "Mb/s", 5, Math.round(i) - 2);
            g.drawString(Math.round((dMaxRXMb / (y1 / i))) + "Mb/s", 5, h - (Math.round(i) - 12));
        }
        g.drawLine(0, h / 2, w, h / 2);

        if (TextInfo) {
            g.setColor(lines_color);
            //final long 
            //        rx_current = ((DeltaArrayRx==null) ? 0 : DeltaArrayRx[0]),
            //        tx_current = ((DeltaArrayTx==null) ? 0 : DeltaArrayTx[0]);

            //g.drawString("TX Max: "+daMaxTx+" bps; TX current: "+tx_current+" bps;", 50, y1+2+FONTSIZE);
            //g.drawString("RX Max: "+daMaxRx+" bps; RX current: "+rx_current+" bps;", 50, y2-2);

            if (DeltaArrayRx != null) {
                g.setFont(big_text_font);
                if (curDate > 0) { 
                    final double cdd = ((float)(new Date().getTime() - curDate)) / 1000f;
                    final double txd = ((DeltaArrayTx[0] * 8f) / (1024f * 1024f)) / cdd;
                    final double rxd = ((DeltaArrayRx[0] * 8f) / (1024f * 1024f)) / cdd;

                    g.drawString("↑" + Math.round(txd) + " Mb/s", w / 3, (h/4)+(big_text_font.getSize() / 2));
                    g.drawString("↓" + Math.round(rxd) + " Mb/s", w / 3, h-((h/4)-(big_text_font.getSize() / 2)));
                    curDate = new Date().getTime();
                } else {
                    curDate = new Date().getTime();
                }
            }
        }
    }
    
    public int GetW() {
        return this.getWidth();
    }
    
    private void __paintTxDelta(Graphics g) {
        if (DeltaArrayTx == null) return;
        //System.err.println("this.getWidth()="+this.getWidth());
        
        g.setColor(tx_color);
        final float max1 = daMaxTx, max2 = (((float)this.getHeight())/DIV2);
        final float max = max2 / max1;
        float line = 0;
        
        for (int i=0; i<(DeltaArrayTx.length-SPACER-1); i++) {
            if (i >= (this.getWidth()-SPACER)) break;
            line = DeltaArrayTx[i] * max;
            if (line > (max2-SPACER)) line = (max2-SPACER);
            if (line>SPACER) g.drawLine(i+SPACER, SPACER, i+SPACER, Math.round(line)); 
        }
    }
    
    private void __paintRxDelta(Graphics g) {
        if (DeltaArrayRx == null) return;
        
        g.setColor(rx_color);
        final float max1 = daMaxRx, max2 = (((float)this.getHeight())/DIV2); /// a=10 b=2   2/10=0.2  a=5*0.2=1.0 
        final float maxz = max2 / max1;
        float line = 0;
        int h = this.getHeight();
        
        for (int i=0; i<(DeltaArrayRx.length-SPACER-1); i++) {
            if (i >= (this.getWidth()-SPACER)) break;
            line = ((float)DeltaArrayRx[i]) * maxz;
            if (line > (max2-SPACER)) line = (max2-SPACER);
            if (line>SPACER) g.drawLine(i+SPACER, h-Math.round(line)-SPACER, i+SPACER, h-1-SPACER); 
            //System.err.println("Math.round(line)="+Math.round(line)+"; maxz="+maxz+"; DeltaArrayRx[i]="+DeltaArrayRx[i]+"; daMaxRx="+daMaxRx+"; this.getHeight()="+this.getHeight());
        }
    }
    
    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        __paintTxDelta(g);
        __paintRxDelta(g);
        __paintLines(g);
        
        
    }

    public long[] toDelta(long[] noDeltaArray) {
        long[] delta = new long[noDeltaArray.length - 1];
        for (int i=0; i<(noDeltaArray.length-1); i++) {
            if (noDeltaArray[i+1] <= 0) break;
            if (noDeltaArray[i] <= 0) break;
            
            delta[i] = noDeltaArray[i] - noDeltaArray[i+1];
            if (i>0) {
                deltaAvg = (deltaAvg + delta[i]) / 2;
            } else {
                deltaAvg = delta[i];
            }
        }
        return delta;
    }
    
    public void setDeltaArrayTx(long[] __DeltaArray) {
        if (__DeltaArray == null) return;
        DeltaArrayTx = __DeltaArray;
        deltaAvgTx = deltaAvg;
        
        daMaxTx = DeltaArrayTx[0];
        daMinTx = DeltaArrayTx[0];
        for (int i=0; i<DeltaArrayTx.length; i++) {
            if (DeltaArrayTx[i] < daMinTx) daMinTx = DeltaArrayTx[i];
            if (DeltaArrayTx[i] > daMaxTx) daMaxTx = DeltaArrayTx[i];
        }
    }
    
    public void setDeltaArrayRx(long[] __DeltaArray) {
        if (__DeltaArray == null) return;
        DeltaArrayRx = __DeltaArray;
        deltaAvgRx = deltaAvg;
        
        daMaxRx = DeltaArrayRx[0];
        daMinRx = DeltaArrayRx[0];
        for (int i=0; i<DeltaArrayRx.length; i++) {
            if (DeltaArrayRx[i] < daMinRx) daMinRx = DeltaArrayRx[i];
            if (DeltaArrayRx[i] > daMaxRx) daMaxRx = DeltaArrayRx[i];
        }
    }
    
    public void DisableTextInfo() {
        TextInfo = false;
    }

    public XDisplay() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(1, 1, 1));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(119, 119, 119)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 582, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 196, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
