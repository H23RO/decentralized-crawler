package ro.h23.dars.webcrawler.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PageTypeClassifier {

    @SerializedName("containsList")
    @Expose
    List<String> containsList;

    @SerializedName("containsNotList")
    @Expose
    List<String> containsNotList;

}
