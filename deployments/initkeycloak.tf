# deployments/init/initkeycloak.tf

terraform {
  required_providers {
    keycloak = {
      source  = "mrparkers/keycloak"
      version = ">= 4.4.0"
    }
  }
}

# 1. Cấu hình Provider
provider "keycloak" {
  client_id = "admin-cli"
  username  = "admin"
  password  = "admin_secret"          # Fixed: Phải khớp với KEYCLOAK_ADMIN_PASSWORD trong docker-compose
  url       = "http://localhost:8180" # Fixed: Gọi qua port 8180 mà container Keycloak đang expose ra host
}

# 2. Tạo Realm
resource "keycloak_realm" "realm" {
  realm        = "fashion-ecommerce"
  enabled      = true
  display_name = "Fashion Ecommerce"
}

# 3. Tạo Role User (Viết thường để khớp với Go Enum)
resource "keycloak_role" "user_role" {
  realm_id    = keycloak_realm.realm.id
  name        = "user" 
  description = "Role mặc định cho khách hàng"
}

# 4. Tạo Role Admin (Viết thường để khớp với Go Enum)
resource "keycloak_role" "admin_role" {
  realm_id    = keycloak_realm.realm.id
  name        = "admin" 
  description = "Role quản trị hệ thống"
}

# 5. Set "user" làm Default Role
# Fixed: Sử dụng keycloak_realm_default_roles thay vì keycloak_default_roles
resource "keycloak_realm_default_roles" "realm_default_roles" {
  realm_id = keycloak_realm.realm.id
  default_roles = [
    "offline_access",
    "uma_authorization",
    keycloak_role.user_role.name
  ]
}

# 6. Tạo Client cho Backend Golang
resource "keycloak_openid_client" "backend_client" {
  realm_id                 = keycloak_realm.realm.id
  client_id                = "backend-api"
  name                     = "Backend API Service"
  enabled                  = true
  access_type              = "CONFIDENTIAL" 
  standard_flow_enabled    = false          
  service_accounts_enabled = true           
}

# =============================================================================
# 7. QUAN TRỌNG: CẤP QUYỀN "ADMIN" CHO BACKEND CLIENT
# =============================================================================

# 7.1. Lấy thông tin role "realm-admin" có sẵn của Keycloak
data "keycloak_openid_client" "realm_management" {
  realm_id  = keycloak_realm.realm.id
  client_id = "realm-management"
}

data "keycloak_role" "realm_admin" {
  realm_id  = keycloak_realm.realm.id
  client_id = data.keycloak_openid_client.realm_management.id
  name      = "realm-admin" 
}

# 7.2. Gán role "realm-admin" vào Service Account của "backend-api"
resource "keycloak_openid_client_service_account_role" "backend_service_account_role" {
  realm_id                = keycloak_realm.realm.id
  service_account_user_id = keycloak_openid_client.backend_client.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management.id
  role                    = data.keycloak_role.realm_admin.name
}

# =============================================================================
# 8. OUTPUTS
# =============================================================================

output "backend_client_id" {
  description = "Client ID sử dụng trong config của Go Backend"
  value       = keycloak_openid_client.backend_client.client_id
}

output "backend_client_secret" {
  description = "Client Secret sử dụng trong config của Go Backend"
  value       = keycloak_openid_client.backend_client.client_secret
  sensitive   = true 
}