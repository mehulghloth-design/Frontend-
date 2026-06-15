from fastapi import FastAPI, Request, Response
from fastapi.middleware.cors import CORSMiddleware
import httpx

app = FastAPI(title="Academic Course Planner Gateway")

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:3000",
        "http://127.0.0.1:3000",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

BACKEND_URL = "http://localhost:8080"

# Headers that should NOT be forwarded to the backend
# (they are hop-by-hop or would confuse the backend)
HOP_BY_HOP_HEADERS = {
    "host", "connection", "keep-alive", "proxy-authenticate",
    "proxy-authorization", "te", "trailers", "transfer-encoding",
    "upgrade", "content-length"
}


def get_forward_headers(request: Request) -> dict:
    """Forward all original request headers to the backend, except hop-by-hop ones."""
    headers = {}
    for key, value in request.headers.items():
        if key.lower() not in HOP_BY_HOP_HEADERS:
            headers[key] = value
    # Always ensure these are set correctly
    headers["Accept"] = "application/json"
    return headers


async def proxy_request(method: str, path: str, request: Request = None, data=None, params=None):
    headers = get_forward_headers(request) if request else {"Accept": "application/json"}

    async with httpx.AsyncClient(timeout=30.0) as client:
        backend_response = await client.request(
            method=method,
            url=f"{BACKEND_URL}{path}",
            json=data,
            params=params,
            headers=headers,
        )

    content_type = backend_response.headers.get("content-type", "application/json")
    return Response(
        content=backend_response.content,
        status_code=backend_response.status_code,
        media_type=content_type,
    )


@app.get("/health")
async def health():
    return {"status": "ok", "service": "gateway"}


@app.post("/api/auth/register")
async def register(request: Request):
    data = await request.json()
    return await proxy_request("POST", "/api/auth/register", request=request, data=data)


@app.post("/api/auth/login")
async def login(request: Request):
    data = await request.json()
    return await proxy_request("POST", "/api/auth/login", request=request, data=data)


@app.get("/api/courses")
async def get_courses(request: Request):
    params = dict(request.query_params)
    return await proxy_request("GET", "/api/courses", request=request, params=params)


@app.get("/api/courses/{course_id}")
async def get_course(course_id: int, request: Request):
    return await proxy_request("GET", f"/api/courses/{course_id}", request=request)


@app.post("/api/courses")
async def create_course(request: Request):
    data = await request.json()
    return await proxy_request("POST", "/api/courses", request=request, data=data)


@app.put("/api/courses/{course_id}")
async def update_course(course_id: int, request: Request):
    data = await request.json()
    return await proxy_request("PUT", f"/api/courses/{course_id}", request=request, data=data)


@app.delete("/api/courses/{course_id}")
async def delete_course(course_id: int, request: Request):
    return await proxy_request("DELETE", f"/api/courses/{course_id}", request=request)


@app.get("/api/plans")
async def get_plans(request: Request):
    return await proxy_request("GET", "/api/plans", request=request)


@app.get("/api/plans/{plan_id}")
async def get_plan(plan_id: int, request: Request):
    return await proxy_request("GET", f"/api/plans/{plan_id}", request=request)


@app.get("/api/plans/user/{user_id}")
async def get_plans_by_user(user_id: int, request: Request):
    params = dict(request.query_params)
    return await proxy_request("GET", f"/api/plans/user/{user_id}", request=request, params=params)


@app.post("/api/plans")
async def create_plan(request: Request):
    data = await request.json()
    return await proxy_request("POST", "/api/plans", request=request, data=data)


@app.put("/api/plans/{plan_id}")
async def update_plan(plan_id: int, request: Request):
    data = await request.json()
    return await proxy_request("PUT", f"/api/plans/{plan_id}", request=request, data=data)


@app.delete("/api/plans/{plan_id}")
async def delete_plan(plan_id: int, request: Request):
    return await proxy_request("DELETE", f"/api/plans/{plan_id}", request=request)


@app.get("/api/planned-courses/{plan_id}")
async def get_planned_courses(plan_id: int, request: Request):
    return await proxy_request("GET", f"/api/planned-courses/{plan_id}", request=request)


@app.post("/api/planned-courses")
async def create_planned_course(request: Request):
    data = await request.json()
    return await proxy_request("POST", "/api/planned-courses", request=request, data=data)


@app.delete("/api/planned-courses/{planned_course_id}")
async def delete_planned_course(planned_course_id: int, request: Request):
    return await proxy_request("DELETE", f"/api/planned-courses/{planned_course_id}", request=request)