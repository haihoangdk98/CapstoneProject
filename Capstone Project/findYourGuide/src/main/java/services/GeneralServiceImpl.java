package services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import configuration.CloudinaryConfig;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class GeneralServiceImpl implements GeneralService {
    private static final long twepoch = 1288834974657L;
    private static final long sequenceBits = 17;
    private static final long sequenceMax = 65536;
    private static volatile long lastTimestamp = -1L;
    private static volatile long sequence = 0L;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private JdbcTemplate jdbcTemplate;
    @Value("${order.server.root.url}")
    private String URL_ROOT_SERVER;

    @Autowired
    public GeneralServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public java.sql.Array createSqlArray(List<String> list) {
        java.sql.Array intArray = null;
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            intArray = conn.createArrayOf("text", list.toArray());
            conn.close();
        } catch (Exception e) {
            logger.error("createSqlArray" + e.getMessage());
        }
        return intArray;
    }

    @Override
    public String[] checkForNull(Array checkArray) {
        String[] checkedString = {"unknown"};
        try {
            if (checkArray != null) {
                checkedString = (String[]) checkArray.getArray();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return checkedString;
    }

    @Override
        public List<String> convertBase64toImageAndChangeName(String[] base64array) {
        List<String> base64List = Arrays.asList(base64array);
        List<String> imageUrls = new ArrayList<>();
        CloudinaryConfig cloudinaryConfig = new CloudinaryConfig();
        Cloudinary cloudinary = cloudinaryConfig.getCloudinary();
        try {
            for (String base64 : base64List) {
                Long uniqueIds = generateLongId();
                Map uploadResult = cloudinary.uploader().upload(base64,
                        ObjectUtils.asMap("public_id",uniqueIds.toString()));
                String imageUrl = (String) uploadResult.get("url");
                imageUrls.add(imageUrl);
                /*byte[] data = Base64.decodeBase64(base64.split(",")[1]);

                Path destinationFile = Paths.get("./src/main/resources/static/images/", uniqueIds.toString() + ".jpg");
                Files.write(destinationFile, data);*/

            }
        } catch (Exception e) {
            logger.error("convertbase64: "+e.toString());
        }
        return imageUrls;
    }

    @Override
    public Long generateLongId() throws Exception {
        long timestamp = System.currentTimeMillis();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        Long id = ((timestamp - twepoch) << sequenceBits) | sequence;
        return id;
    }

    private long tilNextMillis(long lastTimestamp) throws Exception {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
