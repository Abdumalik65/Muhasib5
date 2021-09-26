package sample.Tools;

public class QueryHelper {
    private String sqlWhere = " WHERE ";
    private String sqlOrderBy = " ORDER BY ";
    private String whereString;
    private String orderByString;
    private String yakuniyJumla = "";

    public QueryHelper(String whereString, String orderByString) {
        this.whereString = whereString;
        this.orderByString = orderByString;
        if(!this.whereString.isEmpty()) {
            yakuniyJumla += sqlWhere + this.whereString;
        }
        if(!this.orderByString.isEmpty()) {
            yakuniyJumla += sqlOrderBy + this.orderByString;
        }
    }

    public String getYakuniyJumla() {
        return yakuniyJumla;
    }
}
