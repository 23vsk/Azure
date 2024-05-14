provider "azurerm" {
  features {}
}

terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm" //Example source
      version = "2.78.0"
    }
  }
}
