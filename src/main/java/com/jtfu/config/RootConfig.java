package com.jtfu.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;

@MapperScan("com.jtfu.mapper")
@PropertySource("classpath:db.properties")
public class RootConfig {

    @Value("${db.url}")
    String Url;
    @Value("${db.driver}")
    String Driver;
    @Value("${db.passowrd}")
    String Password;
    @Value("${db.username}")
    String Username;
    @Value("${hadoopIp}")
    String hadoopIp;
    @Bean
    public DataSource dataSource(){
        DruidDataSource dataSource=new DruidDataSource();
        dataSource.setUrl(Url);
        dataSource.setDriverClassName(Driver);
        dataSource.setPassword(Password);
        dataSource.setUsername(Username);
        return dataSource;
    }

    /*@Bean
    public SqlSessionFactoryBean sessionFactoryBean(){
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        return sqlSessionFactoryBean;
    }*/

    @Bean
    public MybatisSqlSessionFactoryBean sessionFactoryBean()throws IOException {
        MybatisSqlSessionFactoryBean factoryBean=new MybatisSqlSessionFactoryBean();
        MybatisConfiguration configuration=new MybatisConfiguration();
        configuration.setLogImpl(StdOutImpl.class);
        configuration.setCacheEnabled(true);
        factoryBean.setConfiguration(configuration);
        factoryBean.setDataSource(dataSource());
        factoryBean.setPlugins(new Interceptor[]{paginationInterceptor()});
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String packageXMLConfigPath = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/mapper/*/*.xml";
        System.err.println(packageXMLConfigPath);
        factoryBean.setMapperLocations(resolver.getResources(packageXMLConfigPath));
        return factoryBean;
    }

 @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor=new PaginationInterceptor();
        paginationInterceptor.setDialectType("mysql");
        return paginationInterceptor;
    }
    @Bean
    public FileSystem fileSystem() throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        // 获取配置文件对象
        conf.set("fs.defaultFS", "hdfs://node:9000");
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem fsSource = FileSystem.get(URI.create("hdfs://node:9000"), conf, "root");
        return fsSource;
    }
     /*  @Bean
    public PerformanceInterceptor performanceInterceptor(){
        //格式化sql语句
        PerformanceInterceptor performanceInterceptor =new PerformanceInterceptor();
        Properties properties =new Properties();
        properties.setProperty("format", "false");
        performanceInterceptor.setProperties(properties);
        return performanceInterceptor;
    }*/
}
