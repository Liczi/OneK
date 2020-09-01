source venv/Scripts/activate
mkdir src/generated
python -m grpc_tools.protoc -I../server/src/main/proto/ --python_out=src/generated --grpc_python_out=src/generated ../server/src/main/proto/server.proto ../server/src/main/proto/action.proto ../server/src/main/proto/common.proto ../server/src/main/proto/state.proto