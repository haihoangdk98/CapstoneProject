package services;

import java.sql.Array;
import java.util.List;

public interface GeneralService {

    java.sql.Array createSqlArray(List<String> list);

    String[] checkForNull(Array checkArray);

    List<String> convertBase64toImageAndChangeName(String[] base64array);

    Long generateLongId() throws Exception;
}
