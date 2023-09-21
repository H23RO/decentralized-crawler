package ro.h23.dars.retrievalcore.persistence.specification;

import org.springframework.data.jpa.domain.Specification;
import ro.h23.dars.retrievalcore.persistence.model.Article;
import ro.h23.dars.retrievalcore.persistence.model.Site;

import javax.persistence.criteria.Expression;
import java.text.MessageFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ArticleSpecifications {

    // https://dimitri.codes/writing-dynamic-queries-with-spring-data-jpa/

    public static Specification<Article> hasPropertyValue(String property, String expression) {
        return (root, query, builder) -> builder.equal(root.get(property), expression);
    }

    public static Specification<Article> hasRegExPropertyValue(String property, String expression) {
        return (root, query, builder) -> {
            Pattern regexPattern = Pattern.compile(expression);
            Expression<String> patternExpression = builder.<String>literal(regexPattern.pattern());
            return builder.equal(builder.function("regexp", Integer.class, root.get(property), patternExpression), 1);
        };
    }

    public static Specification<Article> hasLikePropertyValue(String property, String expression) {
        return (root, query, builder) -> builder.like(root.get(property), "%" + expression + "%");
    }

    public static Specification<Article> hasSite(Site site) {
        return (root, query, builder) -> builder.equal(root.get("site"), site);
    }

    public static Specification<Article> isBetweenDates(String property, Date start, Date end) {
        return (root, query, builder) -> builder.between(root.get(property), start, end);
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }

}
