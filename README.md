# Quarkus Sample Project

**Mục đích của dự án**                          
Dự án này là một ứng dụng mẫu được xây dựng bằng Quarkus, một framework Java hiện đại cho phép phát triển các ứng dụng cloud-native và microservices với hiệu suất cao.       
Mục tiêu của dự án là minh họa cách sử dụng Quarkus để xây dựng các API RESTful bảo mật, tích hợp với cơ sở dữ liệu PostgreSQL, và giám sát hiệu năng bằng Prometheus và Grafana.           
Dự án tập trung vào các tính năng sau:             

+ Xác thực người dùng thông qua JWT (JSON Web Token).               
+ Quản lý người dùng với cơ sở dữ liệu PostgreSQL.        
+ Giám sát và kiểm tra chịu tải với Prometheus, Grafana và k6.       

**Công nghệ sử dụng**      
Dự án sử dụng các công nghệ và thư viện sau:          

Quarkus: Framework Java cho ứng dụng cloud-native.       
RESTEasy Reactive: Để xây dựng các API RESTful.        
Hibernate ORM Panache: ORM đơn giản hóa việc tương tác với cơ sở dữ liệu.        
PostgreSQL: Cơ sở dữ liệu quan hệ để lưu trữ dữ liệu người dùng.       
SmallRye JWT: Để sinh và xác thực JWT.     
Micrometer Prometheus: Để xuất metrics giám sát.       
Docker & Docker Compose: Để container hóa ứng dụng và các dịch vụ phụ trợ.       
Grafana & Prometheus: Để giám sát và trực quan hóa metrics.       
k6: Để kiểm tra chịu tải.         

**Cấu trúc dự án**     
         
src/main/java/com/example/: Chứa mã nguồn chính của ứng dụng.             
AuthResource.java: Định nghĩa các endpoint REST cho đăng nhập và đăng ký.         
User.java: Entity đại diện cho người dùng trong cơ sở dữ liệu.       
         

src/main/resources/:        
application.properties: Cấu hình ứng dụng, bao gồm kết nối cơ sở dữ liệu và JWT.        
privateKey.pem: Khóa riêng để ký JWT.          
       

monitoring/: Chứa cấu hình cho Prometheus và Grafana.      
Dockerfile: Để xây dựng image Docker của ứng dụng.      
docker-compose.yml: Để chạy ứng dụng và cơ sở dữ liệu PostgreSQL.         
load-test.js: Script k6 để kiểm tra chịu tải.           
        
**Hướng dẫn cài đặt và chạy**
*Yêu cầu*         

Java 17+      
Maven 3.8.1+      
Docker & Docker Compose         
openssl (để tạo khóa JWT)           
*Quy trình*                  
Bước 1: Clone repository         
git clone https://github.com/nguyendangnamphong/loginAndRegister_QuarkusExample                       
cd quarkus-sample         

Bước 2: Tạo khóa JWT         
openssl genrsa -out src/main/resources/privateKey.pem 2048          

Bước 3: Build ứng dụng           
mvn clean package -Dquarkus.package.jar.enabled=true -Dquarkus.package.jar.type=uber-jar         

Bước 4: Build Docker image          
docker build -t quarkus-sample .                

Bước 5: Chạy ứng dụng với Docker Compose             
docker-compose up -d          

Bước 6: Chạy Prometheus và Grafana            
cd monitoring           
docker-compose up -d               

Bước 7: Truy cập ứng dụng            

API: http://localhost:8080/api/login-json          
Prometheus: http://localhost:9090           
Grafana: http://localhost:3000 (đăng nhập với admin/admin)          

*Tính năng chính*         

Đăng ký và đăng nhập người dùng với mã hóa mật khẩu bằng bcrypt.           
Sinh JWT cho người dùng sau khi đăng nhập thành công.              
Giám sát metrics với Prometheus và trực quan hóa trên Grafana.          
Kiểm tra chịu tải với k6 để đánh giá hiệu năng API.              
