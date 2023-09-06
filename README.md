# Spring解体新書第2版の変更点(2023/9/4時点)

本の執筆時点から SprignBoot 自身を始め多くのライブラリのバージョンが変わっています。それらバージョン変更に伴う修正点をここにまとめ、ソースコードとともにここに公開します。

## 1章 Springの概要

変更なし

## 2章 開発環境の構築

Java17が必要なため、Pleiades から最新の Eclipse をインストールします。これには STS や Lombok 等も含まれているため、Eclipse 以外は別途インストールする必要はありません。

## 3章 Hello World ・・・簡単なサンプル

### 3.3 データベースから値を取得する

Spring起動時に実行するSQLの設定項目が変更されています。

[application.properties]

変更前

```properties
spring.datasource.sql-script-encoding=UTF-8
spring.datasource.initialize=true
spring.datasource.schema=classpath:schema.sql
spring.datasource.data=classpath:data.sql
```

変更後

```properties
spring.sql.init.encoding=UTF-8
spring.sql.init.mode=ALWAYS
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
```

## 4章 Webアプリケーションの概要

変更なし

## 5章 Dependency Injection(依存性の注入)

変更なし

## 6章 バインド&バリデーション(入力チェック)

### 6.1.1 ライブラリの仕様・・・webjars

webjars-locator のバージョン 0.47 (2023/9/4時点の最新)が使用できます。

[pom.xml]

```xml
<!-- webjars-locator -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator</artifactId>
    <version>0.47</version>
</dependency>
```

### 6.3.1 バリデーションの実装

Spring Boot 3 から JavaEE が JakartaEE 9になったため、パッケージ名が javax.\* となっているものをすべて jakarta.\* に変更する必要があります。

[SignupForm.java]

変更前

```java
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
```

変更後

```java
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
```

(上記だけでなく、以降に出てくるすべてのコードの javax パッケージを変更します)

## 7章 画面レイアウト

変更なし

## 8章 MyBatis

### 8.2 MyBatis基本編

Spring Boot 3.0 - 3.1 に対応した MyBatis-Spring-Boot-Starter のバージョンは 3.0 なので[(MyBatis公式サイト)](http://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)、バージョン 3.0.2 (2023/9/4時点での最新)を使用します。
また ModelMapper-Spring も新しいバージョンが使えるので 3.1.1 (2023/9/4時点の最新)にします。

[pom.xml]

```xml
<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.2</version>
</dependency>
<!-- Model Mapper -->
<dependency>
    <groupId>org.modelmapper.extensions</groupId>
    <artifactId>modelmapper-spring</artifactId>
    <version>3.1.1</version>
</dependency>
```

## 9章 AOP

変更なし

## 10章 エラー処理

変更なし

## 11章 Springセキュリティ

### 11.2.1 直リンクの禁止

Spring Boot 3 で Spring-Boot-Starter-Security を入れると SpringSecurity のバージョンが 5 ではなく 6 となります。それに合わせて Thymeleaf拡張ライブラリ(セキュリティ)を Thymeleaf-Extras-SpringSecurity6 に変更します。

[pom.xml]

```xml
<!-- SpringSecurity -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!-- Thymeleaf拡張ライブラリ-->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

SpringSecurity 5.7 以降から、セキュリティ設定クラスの書き方が大きく変更されています。

- WebSecurityConfigurerAdapter を継承せず、configure で行っている HttpSecurity http へのセキュリティ設定は SecurityFilterChain を Bean 定義して行う
- webjars や css などはセキュリティ対象外として設定するのではなく、ログイン不要ページとして設定する
- authorizeRequests()ではなくauthorizeHttpRequests()を使う
- 設定はラムダ式で記述する
- antMatchers()ではなくrequestMatchers()を使う
- requestMatchers()の引数に、文字列でパスを指定("/login"など)するとエラーになる(※)ため、文字列ではなく MvcRequestMatcher インスタンスをセットする
具体的には、MvcRequestMatcher.Builder を Bean 登録して securityFilterChainの引数 mvc に DI でセットし、mvc.pattern("/user/signup") のようにして指定する
※SpringMVCの管轄の "/" とH2データベースの管轄の "/h2-console" が並存しており、文字列だけでは SpringMVC の管轄かどうかが判断できないためと思われる
- 一般的な静的リソースの場所の指定(/webjars/\*\*, /css/\*\*, /js/\*\*)は、PathRequest.toStaticResources().atCommonLocations() としてまとめて指定する
- "/login" への直リンク許可設定は、次節のログイン処理設定で行うためここではまだ行わない
- csrf().disable() は非推奨となったため、ラムダ式でcsrf(csrf -> csrf.disabe()) のように指定する
- H2コンソールのパス("/h2-console/\*\*")は、PathRequest.toH2Console() として指定する
- H2コンソールを表示させるためには、さらに以下の設定が必要

    ```java
    http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));
    http.csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()));
    ```

[SecurityConfig.java]

```java
package com.example.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers(mvc.pattern("/user/signup")).permitAll()
                .anyRequest().authenticated()
        );

        http.headers(headers -> headers
                .frameOptions(FrameOptionsConfig::disable)
        );

        // CSRF 対策を無効に設定 (一時的)
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(PathRequest.toH2Console())
                .disable()
        );

        return http.build();
    }
}
```

#### 403 エラーのカスタム画面

ここでの 403 エラーでは、カスタムエラー画面が出ずに「localhost へのアクセスが拒否されました」となります。原因はまだ不明ですが気にせず進めてください。

### 11.2.2 ログイン処理

セキュリティ設定クラスの http.formLogin() もラムダ式で設定します。またこの設定のメソッドチェーンの最後に .permitAll() を追加し、ログイン関連処理("/login" 等)の直リンクを許可します。

[SecurityConfig.java]

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        ...(省略)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers(mvc.pattern("/user/signup")).permitAll()
                .anyRequest().authenticated()
        );

        // 変更点 ここから
        http.formLogin(login -> login
                .loginProcessingUrl("/login")
                .loginPage("/login")
                .failureUrl("/login?error")
                .usernameParameter("userId")
                .passwordParameter("password")
                .defaultSuccessUrl("/user/list", true)
                .permitAll()
        );
        // ここまで

        http.headers(headers -> headers
                .frameOptions(FrameOptionsConfig::disable)
        );

        // CSRF 対策を無効に設定 (一時的)
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(PathRequest.toH2Console())
                .disable()
        );

        return http.build();
    }
}
```

### 11.2.3 インメモリ認証

インメモリ認証の設定は auth.inMemoryAuthentication() メソッドではなく、 InMemoryUserDetailsManager を Bean 定義して行うように変更します。この中では SpringSecurity で用意されている User クラスを使って user と admin の２人のユーザーを作成しています。

[SecurityConfig.java]

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        ...(省略)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        ...(省略)
    }

    // 変更点 ここから
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("user"))
                .roles("GENERAL")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("admin"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    // ここまで
}
```

#### ログイン失敗時のメッセージ変更

原因不明ですが、メッセージプロパティを修正してもメッセージが変更されません。

### 11.2.4 パスワードの暗号化

パスワードの暗号化は本の通りです。

[SecurityConfig.java]

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    // 変更点 ここから
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // ここまで

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        ...(省略)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        ...(省略)
    }

    // 変更点 ここから
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user"))
                .roles("GENERAL")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    // ここまで
}
```

### 11.2.5 ユーザーデータ認証

本の通りに UserDetailService の実装クラス UserDetailServiceImpl を作成すれば、SecurityConfig から InMemoryUserDetailsManager の Bean 定義を削除するだけでユーザーデータ認証が行われるようになります。

[SecurityConfig.java]

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        ...(省略)
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        ...(省略)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        ...(省略)
    }

    // 変更点 ここから
    /*
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user"))
                .roles("GENERAL")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    */
    // ここまで
}
```

### 11.2.6 ログアウト処理

http.logout()もラムダ式で記述します。http.*() のメソッドはメソッドチェーンで繋げて書くこともできます。

[SecurityConfig.java]

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        ...(省略)
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        ...(省略)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers(mvc.pattern("/user/signup")).permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(login -> login
                .loginProcessingUrl("/login")
                .loginPage("/login")
                .failureUrl("/login?error")
                .usernameParameter("userId")
                .passwordParameter("password")
                .defaultSuccessUrl("/user/list", true)
                .permitAll()
        // 変更点 ここから
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
        // ここまで
        );

        http.headers(headers -> headers
                .frameOptions(FrameOptionsConfig::disable)
        );

        // CSRF 対策を無効に設定 (一時的)
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(PathRequest.toH2Console())
                .disable()
        );

        return http.build();
    }

    ...(省略)
}
```

### 11.2.7 CSRF対策

コメントアウトするのはラムダ式内の.disable()のみで、.ignoringRequestMatchers(PathRequest.toH2Console())は残します。

[SecurityConfig.java]

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        ...(省略)
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        ...(省略)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        ...(省略)

        // CSRF 対策を無効に設定 (一時的)
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(PathRequest.toH2Console())
                // 変更点 ここから
                //.disable()
                // ここまで
        );

        return http.build();
    }

    ...(省略)
}
```

### 11.3.1 URLの認可

セキュリティ設定クラスへのURL認可の設定も、antMatchers() ではなく requestMatchers() と mvc.pattern() を使います。

[SecurityConfig.java]

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // このアノテーションはこのアプリではなくてよい
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        ...(省略)
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        ...(省略)
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers(mvc.pattern("/user/signup")).permitAll()
                // 変更点 ここから
                .requestMatchers(mvc.pattern("/admin")).hasAuthority("ROLE_ADMIN")
                // ここまで
                .anyRequest().authenticated()
        );

        ...(省略)
    }

    ...(省略)
}
```

## 12章 REST

### 12.2.3 検索

DataTables は新しいバージョンが使えるので 1.13.5 (2023/9/4時点の最新) を使用します。それに伴い DataTables の言語設定ファイル名が変わっているので、list.js にも修正が必要です。

[pom.xml]

```xml
<!-- datatables -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>datatables</artifactId>
    <version>1.13.5</version>
</dependency>
<!-- datatables-plugins -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>datatables-plugins</artifactId>
    <version>1.13.5</version>
    <scope>runtime</scope>
</dependency>
```

[list.js]

```javascript
'use strict'

var userData = null;
var table = null;

jQuery(function ($) {
    ...(省略)
});

function search() {
    ...(省略)
}

function createDataTables() {

    if (table != null) {
        table.destroy();
    }

    table = $('#user-list-table').DataTable({
        language: {
            url: '/webjars/datatables-plugins/i18n/ja.json'
        },
        ...(省略)
    });
}
```

## 13章 Spring Data JPA

javax パッケージは jakarta パッケージに修正してください。(MUser, Department, SalaryKey, Salary の4クラス)
