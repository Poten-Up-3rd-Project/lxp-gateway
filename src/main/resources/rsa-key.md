### 생성 방법

```bash
# 1. RSA 개인키 생성 (2048-bit)
openssl genrsa -out private_key.pem 2048

# 2. 개인키에서 공개키 추출
openssl rsa -in private_key.pem -pubout -out public_key.pem

# 3. PKCS#8 형식으로 개인키 변환 (Java에서 사용하기 위함)
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private_key.pem -out private_key_pkcs8.pem

# 4. Base64 인코딩된 값 추출 (application.yml에 넣을 값)
# Private Key
cat private_key_pkcs8.pem | grep -v "BEGIN" | grep -v "END" | tr -d '\n'

# Public Key  
cat public_key.pem | grep -v "BEGIN" | grep -v "END" | tr -d '\n'
```
