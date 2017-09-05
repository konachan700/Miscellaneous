package jneko;

public interface JNekoEventListener {
    public void OnSetUserIPData(String DID, String eltx, String slot, String ip, String mac);
    public void OnSetUserInfo(String DID, String userFullName, String userLogin, String userPassword, String groupID, 
            String tpID, String eltx, String houseID, String streetID, String townID, String flatNumber, String mobilePhone, String email);
    public void OnAddNewUser(String DID, String userFullName, String userLogin, String userPassword, String groupID, 
            String tpID, String eltx, String houseID, String streetID, String townID, String flatNumber, String mobilePhone, String email);
    public void OnTarifUpdated(String DID, String tname, String u, String d, String cpm, String state);
    public void OnTarifCreated(String tname, String u, String d, String cpm, String state);
    public void OnUpdateHTS(String tableName, String ID, String value, String alias);
    public void OnDeleteHTS(String tableName, String ID);
    public void OnAddHTS(String tableName, String value, String alias);
    public void OnDoPaymentsClass1(String DID, long moneyCount, String moneyComment, boolean minus);
}
