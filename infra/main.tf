terraform {
  required_version = ">= 1.5.0"
  required_providers {
    helm = {
      source = "hashicorp/helm"
      version = "3.0.2"
    }
    cloudflare = {
      source = "cloudflare/cloudflare"
      version = "5.10.1"
    }
  }
}
