variable "cloudflare_zone_id" {
  type = string
  sensitive = true
}

variable "cloudflare_api_token" {
  type = string
  sensitive = true
}

variable "docker_registry_server" {
  type = string
  default = "ghcr.io"
}

variable "docker_registry_username" {
  type = string
}

variable "docker_registry_password" {
  type = string
  sensitive = true
}

variable "docker_registry_email" {
  type = string
  sensitive = true
}
