package com.pankaj.springjdbc.jdbc;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.ResultSetExtractorImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class JdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdbcApplication.class, args);
    }
}

@Order(1)
@Component
@Log4j2
class QueryCustomersAndOrdersCount implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public QueryCustomersAndOrdersCount(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StringUtils.line();
        String sql = "select c.* , (select count(o.id) from orders o where o.customer_fk = c.id) as count from customers c";

        Collection<CustomerOrderReport> reports = this.jdbcTemplate.query("select c.* , (select count(o.id) from orders o where o.customer_fk = c.id) as count from customers c",
                (rs, rowNum) -> new CustomerOrderReport(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("count")));

        reports.forEach(log::info);

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerOrderReport {
        private Long customerId;
        private String name, email;
        private int orderCount;
    }
}

@Order(2)
@Component
@Log4j2
class QueryCustomersAndOrders implements ApplicationRunner {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
        private Long customerId;
        private String name, email;
        private Set<Order> orders = new HashSet<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Order {
        private Long id;
        private String sku;

    }

    private final JdbcTemplate jdbcTemplate;

    public QueryCustomersAndOrders(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StringUtils.line();
        ResultSetExtractor<Collection<Customer>> rse = rs -> {
            Map<Long, Customer> customerMap = new ConcurrentHashMap<>();
            Customer currentCustomer = null;
            while (rs.next()) {
                long id = rs.getLong("cid");
                if (currentCustomer == null || currentCustomer.getCustomerId() != id) {

                    currentCustomer = new Customer(rs.getLong("cid"),
                            rs.getString("name"),
                            rs.getString("email"),
                            new HashSet<>());
                }
                String customerFk = rs.getString("customer_fk");
                if (customerFk != null) {
                    Long oid = rs.getLong("oid");
                    String sku = rs.getString("sku");
                    Order order = new Order(oid, sku);
                    currentCustomer.getOrders().add(order);
                    customerMap.put(currentCustomer.getCustomerId(), currentCustomer);
                }
            }
            return customerMap.values();
        };
        Collection<Customer> customers = this.jdbcTemplate.query(
        		"select c.id as cid,c.* ,o.id as oid,o.* from customers c left join orders o on c.id = o.customer_fk order by cid"
				, rse);
        customers.forEach(log::info);
        customers = customers.stream()
                .map(c->{
                    boolean hasNullOrderValues = c.getOrders().stream().anyMatch(o -> o.getId() == null);
                    if (hasNullOrderValues)
                        c.setOrders(new HashSet<>());
                    return c;
                })
                .collect(Collectors.toSet());
    }
}

@Order(3)
@Component
@Log4j2
class QueryCustomersAndOrdersSimpleFlatMapper implements ApplicationRunner {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
        private Long id;
        private String name, email;
        private Set<Order> orders = new HashSet<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Order {
        private Long id;
        private String sku;

    }

    private final JdbcTemplate jdbcTemplate;

    public QueryCustomersAndOrdersSimpleFlatMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StringUtils.line();
        ResultSetExtractorImpl<Customer> rse = JdbcTemplateMapperFactory
                                                .newInstance()
                                                .addKeys("id")
                                                .newResultSetExtractor(Customer.class);
        Collection<Customer> customers = this.jdbcTemplate.query(
                "select c.id as id,c.name as name,c.email as email  ,o.id as orders_id,o.sku as orders_sku from customers c left join orders o on o.customer_fk = c.id order by c.id"
                , rse);
        customers.forEach(log::info);
        /***
         * //AGGREGATE
         * 1,pankaj,2005pank@gmail.com,	1, 	abc,
         * 1,pankaj,2005pank@gmail.com,	2, 	def,
         * 1,pankaj,2005pank@gmail.com,	2, 	ghi,
         * //AGGREGATE
         * 2,jane,jane@jane.com,	3, 	ghi,
         * 2,jane,jane@jane.com,	5, 	ghi,
         */
    }
}

@Order(4)
@Configuration
@Log4j2
class JdbcTemplateWriter implements ApplicationRunner{
    private final JdbcTemplate template;

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> new Customer(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"));

    public JdbcTemplateWriter(JdbcTemplate template) {
        this.template = template;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
        private Long id;
        private String name, email;
    }

    public Customer insert(String name,String email){
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        this.template.update(con -> {
            PreparedStatement preparedStatement =
                    con.prepareStatement("insert into customers(name,email) values(?,?)",
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            return preparedStatement;
        },generatedKeyHolder);

        long idOfNewCustomer = generatedKeyHolder.getKey().longValue();

        return template.queryForObject("select c.* from customers c where c.id=?",
                customerRowMapper,idOfNewCustomer);

    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        StringUtils.line();
        Stream.of("A","B","C").forEach(name -> insert(name, name + "@" + name + ".com"));
        log.info("RESULTS");
        this.template.query("select * from customers order by id",customerRowMapper).forEach(log::info);

    }
}

@Order(5)
@Configuration
@Log4j2
class JdbcObjectWriter implements ApplicationRunner{

    private final SimpleJdbcInsert simpleJdbcInsert;
    private final CustomerMappingSqlQuery all,byId;


    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> new Customer(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"));

    public JdbcObjectWriter(DataSource ds) {
        ;
        this.simpleJdbcInsert = new SimpleJdbcInsert(ds)
                                    .withTableName("customers")
                                    .usingGeneratedKeyColumns("id");
        this.all = new CustomerMappingSqlQuery(ds,"select * from customers");
        this.byId = new CustomerMappingSqlQuery(ds,"select * from customers where id = ?",
                new SqlParameter("id",Types.INTEGER));
    }


    private static class CustomerMappingSqlQuery extends MappingSqlQuery<Customer> {
        public CustomerMappingSqlQuery(DataSource ds, String sql, SqlParameter ... params) {
            setDataSource(ds);
            setSql(sql);
            setParameters(params);
            afterPropertiesSet();

        }

        @Override
        protected Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(rs.getLong("id"),rs.getString("name"),rs.getString("email"));
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
        private Long id;
        private String name, email;
    }

    public Customer insert(String name,String email){
        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("email",email);
        long id = this.simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return this.byId.findObject(id);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        StringUtils.line();
        Stream.of("A","B","C").forEach(name -> insert(name, name + "@" + name + ".com"));
        this.all.execute().forEach(log::info);

    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    @Id
    private Long id;
    private String name, email;
}

@Repository
interface CustomerRepository extends CrudRepository<Customer,Long>{
    @Query("select * from customers c where c.email = :email")
    Collection<Customer> findByEmail(@Param("email") String email);

}

@Component
@Log4j2
@Order(6)
class SpringDataJdbc implements ApplicationRunner{
    private final CustomerRepository customerRepository;

    public SpringDataJdbc(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StringUtils.line();

        Stream.of("A","B","C").forEach(
                name -> customerRepository.save(new Customer(null,name,name + "@" + name +".com"))
        );
        customerRepository.findAll().forEach(log::info);
        customerRepository.save(new Customer(null,"foo","bar"));
        customerRepository.findByEmail("bar").forEach(log::info);

    }
}
@Configuration
@EnableJdbcRepositories
class SpringDataJdbcConfiguration{

    @Bean
    NamingStrategy namingStrategy(){
        return new NamingStrategy() {
            @Override
            public String getTableName(Class<?> type) {
                return type.getSimpleName().toLowerCase() + "s";
            }
        };
    }

}

@Log4j2
abstract class StringUtils {
    public static void line() {
        log.info("===================================");
    }
}
