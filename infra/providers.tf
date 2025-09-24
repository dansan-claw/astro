provider "helm" {
  kubernetes = {
    config_path = "./creds/kubeconfig"
  }
}

provider "kubernetes" {
  config_path = "kubeconfig"
}

provider "cloudflare" {
  api_token = var.cloudflare_api_token
}

