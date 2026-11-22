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
        title   = "Latency (p95 / p99)"
        gridPos = { x = 12, y = 0, w = 12, h = 8 }
        datasource = {
          type = "prometheus"
          uid  = grafana_data_source.prometheus.uid
        }
        fieldConfig = {
          defaults = { unit = "s" }
        }
        targets = [
          {
            refId        = "A"
            expr         = "histogram_quantile(0.95, sum by (le) (rate(http_server_requests_seconds_bucket{job=\"event-catalog\", uri!~\"/actuator.*\"}[5m])))"
            legendFormat = "p95"
          },
          {
            refId        = "B"
            expr         = "histogram_quantile(0.99, sum by (le) (rate(http_server_requests_seconds_bucket{job=\"event-catalog\", uri!~\"/actuator.*\"}[5m])))"
            legendFormat = "p99"
          }
        ]
      }
    ]
  })
}

resource "grafana_dashboard" "booking" {
  folder = grafana_folder.pathwatch.uid

  config_json = jsonencode({
    title   = "Booking Service - Overview"
    uid     = "booking-service-overview"
    time    = { from = "now-15m", to = "now" }
    refresh = "10s"

    panels = [
      {
        id      = 1
        type    = "timeseries"
        title   = "Reservation attempts by outcome (per second)"
        gridPos = { x = 0, y = 0, w = 12, h = 8 }
        datasource = {
          type = "prometheus"
          uid  = grafana_data_source.prometheus.uid
        }
        targets = [{
          refId        = "A"
          expr         = "sum by (outcome) (rate(booking_reservations_total{job=\"booking-service\"}[1m]))"
          legendFormat = "{{outcome}}"
        }]
      },
      {
        id      = 2
        type    = "timeseries"
        title   = "Confirmed vs expired reservations (per second)"
        gridPos = { x = 12, y = 0, w = 12, h = 8 }
        datasource = {
          type = "prometheus"
          uid  = grafana_data_source.prometheus.uid
        }
        targets = [
          {
            refId        = "A"
            expr         = "rate(booking_reservations_confirmed_total{job=\"booking-service\"}[1m])"
            legendFormat = "confirmed"
          },
          {
            refId        = "B"
            expr         = "rate(booking_reservations_expired_total{job=\"booking-service\"}[1m])"
            legendFormat = "expired"
          }
        ]
      },
      {
        id      = 3
        type    = "timeseries"
        title   = "Active seat holds"
        gridPos = { x = 0, y = 8, w = 12, h = 8 }
        datasource = {
          type = "prometheus"
          uid  = grafana_data_source.prometheus.uid
        }
        targets = [{
          refId        = "A"
          expr         = "booking_reservations_held{job=\"booking-service\"}"
          legendFormat = "held"
        }]
      },
      {
        id      = 4
        type    = "timeseries"
        title   = "Calls to event-catalog by outcome (per second)"
        gridPos = { x = 12, y = 8, w = 12, h = 8 }
        datasource = {
          type = "prometheus"
          uid  = grafana_data_source.prometheus.uid
        }
        targets = [{
          refId        = "A"
          expr         = "sum by (outcome) (rate(http_client_requests_seconds_count{job=\"booking-service\"}[1m]))"
          legendFormat = "{{outcome}}"
        }]
      }
    ]
  })
}