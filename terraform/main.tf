resource "grafana_folder" "pathwatch" {
  title = "Pathwatch"
  uid   = "pathwatch"
}

resource "grafana_data_source" "prometheus" {
  type       = "prometheus"
  name       = "Prometheus"
  uid        = "prometheus"
  url        = "http://prometheus:9090"
  is_default = true
}

resource "grafana_dashboard" "event_catalog" {
  folder = grafana_folder.pathwatch.uid

  config_json = jsonencode({
    title = "Event Catalog - Overview"
    uid   = "event-catalog-overview"

    panels = [
      {
        id      = 1
        type    = "timeseries"
        title   = "Request rate by endpoint (req/s)"
        gridPos = { x = 0, y = 0, w = 12, h = 8 }
        datasource = {
          type = "prometheus"
          uid  = grafana_data_source.prometheus.uid
        }
        targets = [{
          refId        = "A"
          expr         = "sum by (uri) (rate(http_server_requests_seconds_count{job=\"event-catalog\", uri!~\"/actuator.*\"}[1m]))"
          legendFormat = "{{uri}}"
        }]
      },
      {
        id      = 2
        type    = "timeseries"
        title   = "Average latency"
        gridPos = { x = 12, y = 0, w = 12, h = 8 }
        datasource = {
          type = "prometheus"
          uid  = grafana_data_source.prometheus.uid
        }
        fieldConfig = {
          defaults = { unit = "s" }
        }
        targets = [{
          refId        = "A"
          expr         = "sum(rate(http_server_requests_seconds_sum{job=\"event-catalog\", uri!~\"/actuator.*\"}[1m])) / sum(rate(http_server_requests_seconds_count{job=\"event-catalog\", uri!~\"/actuator.*\"}[1m]))"
          legendFormat = "avg latency"
        }]
      }
    ]
  })
}