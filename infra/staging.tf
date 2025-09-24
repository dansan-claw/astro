resource "kubernetes_namespace" "astro_staging_namespace" {
  metadata {
    name = "astro-staging"
  }
}

# convert env files to k8s config maps
locals {
 service_configs_staging = {
    bot = {
      config_name = "bot-config"
      env_path    = "bot/staging.env"
    }
    central-api = {
      config_name = "central-api-config"
      env_path    = "central-api/staging.env"
    }
  }
  
  # Parse all env files
  parsed_env_files_staging = {
    for service_key, config in local.service_configs_staging :
    service_key => {
      for line in split("\n", file("${path.root}/../env/${config.env_path}")) :
      element(split("=", line), 0) => join("=", slice(split("=", line), 1, length(split("=", line))))
      if length(trim(line, " \t\r\n")) > 0 && 
         !startswith(trim(line, " \t\r\n"), "#") &&
          strcontains(line, "=")
    }
  }
}

resource "kubernetes_config_map" "service_configs_staging" {
  for_each = local.service_configs_staging
  
  metadata {
    name      = each.value.config_name
    namespace = kubernetes_namespace.astro_staging_namespace.metadata[0].name
  }
  
  data = local.parsed_env_files_staging[each.key]
}

# authentication for pulling docker images from ghcr
resource "kubernetes_secret" "ghcr_staging" {
  metadata {
    name = "docker-registry"
    namespace = kubernetes_namespace.astro_staging_namespace.metadata[0].name
  }

  type = "kubernetes.io/dockerconfigjson"

  data = {
    ".dockerconfigjson" = jsonencode({
      auths = {
        (var.docker_registry_server) = {
          "username" = var.docker_registry_username
          "password" = var.docker_registry_password
          "email"    = var.docker_registry_email
          "auth"     = base64encode("${var.docker_registry_username}:${var.docker_registry_password}")
        }
      }
    })
  }
}

# authentication for bigquery
resource "kubernetes_secret" "gcp_bigquery_creds_staging" {
  metadata {
    name      = "gcp-bigquery-creds"
    namespace = kubernetes_namespace.astro_staging_namespace.metadata[0].name
  }

  data = {
    "gcp-bigquery-creds.json" = filebase64("./creds/gcp-bigquery-creds.json")
  }

  type = "Opaque"
}
