import React, { useState, useEffect, useCallback } from "react";
import {
  getAllTasks,
  searchTasks,
  createTask,
  deleteTask,
  completeTask,
  updateTask,
  getTaskById,
} from "./api";
import "./App.css";
import { FaCheck, FaTrash } from "react-icons/fa";

function App() {
  const [tasks, setTasks] = useState([]);
  const [newTask, setNewTask] = useState({ title: "", description: "" });
  const [searchQuery, setSearchQuery] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Estados para edición
  const [editingTaskId, setEditingTaskId] = useState(null);
  const [editingTaskData, setEditingTaskData] = useState({ title: "", description: "" });

  // Estados para modal
  const [selectedTask, setSelectedTask] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loadingTask, setLoadingTask] = useState(false);

    const openModal = async (taskId) => {
      setLoadingTask(true);
      setIsModalOpen(true);
      try {
        const task = await getTaskById(taskId);
        setSelectedTask(task);
      } catch (error) {
        console.error("Error fetching task:", error);
      } finally {
        setLoadingTask(false);
      }
    };

    const closeModal = () => {
      setSelectedTask(null);
      setIsModalOpen(false);
    };

    const fetchTasks = useCallback(async () => {
        const data = await getAllTasks(page);
        setTasks(data.content);
        setTotalPages(data.totalPages);
    }, [page]); // ✅ ahora depende solo de 'page'

    useEffect(() => {
        fetchTasks();
    }, [fetchTasks]); // ✅ ya no da warning

  const handleCreateTask = async () => {
    if (newTask.title && newTask.description) {
      await createTask(newTask);
      setNewTask({ title: "", description: "" });
      fetchTasks();
    }
  };

  const handleDeleteTask = async (id) => {
    await deleteTask(id);
    fetchTasks();
  };

  const handleCompleteTask = async (id, completed) => {
    await completeTask(id, completed);
    fetchTasks();
  };

  const handleSearch = async () => {
    if (searchQuery) {
      const data = await searchTasks(searchQuery, page);
      setTasks(data.content);
      setTotalPages(data.totalPages);
    } else {
      fetchTasks();
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
    }
  };

  return (
    <div className="App">
      <h1>Task Manager</h1>

      {/* Barra de búsqueda */}
      <div className="search-bar">
        <input
          type="text"
          placeholder="Search tasks..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button onClick={handleSearch}> 🔍</button>
      </div>

      {/* Crear tarea */}
      <div className="create-task">
        <h2>Create Task</h2>
        <input
          type="text"
          placeholder="Title"
          value={newTask.title}
          onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
        />
        <input
          type="text"
          placeholder="Description"
          value={newTask.description}
          onChange={(e) =>
            setNewTask({ ...newTask, description: e.target.value })
          }
        />
        <button onClick={handleCreateTask}>Add Task</button>
      </div>

      {/* Lista de tareas */}
      <h2>Task List</h2>
      <div className="task-list">
        {tasks.map((task) => (
          <div
            className="task-card"
            key={task.id}
            onClick={() => openModal(task.id)}
          >
            {editingTaskId === task.id ? (
              <>
                <input
                  type="text"
                  value={editingTaskData.title}
                  onChange={(e) =>
                    setEditingTaskData({
                      ...editingTaskData,
                      title: e.target.value,
                    })
                  }
                />
                <input
                  type="text"
                  value={editingTaskData.description}
                  onChange={(e) =>
                    setEditingTaskData({
                      ...editingTaskData,
                      description: e.target.value,
                    })
                  }
                />
                <div className="task-actions">
                  <button
                    onClick={async () => {
                      await updateTask(task.id, editingTaskData);
                      setEditingTaskId(null);
                      fetchTasks();
                    }}
                  >
                    💾
                  </button>
                  <button onClick={() => setEditingTaskId(null)}>❌</button>
                </div>
              </>
            ) : (
              <>
                <h3 className={task.completed ? "completed" : ""}>
                  {task.title}
                </h3>
                <p className={task.completed ? "completed" : ""}>
                  {task.description}
                </p>
                <div className="task-actions">
                  <button
                    onClick={(e) => {
                      e.stopPropagation(); // evita que se abra modal
                      handleCompleteTask(task.id, !task.completed);
                    }}
                    title="Complete"
                  >
                    <FaCheck />
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteTask(task.id);
                    }}
                    title="Delete"
                  >
                    <FaTrash />
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      setEditingTaskId(task.id);
                      setEditingTaskData({
                        title: task.title,
                        description: task.description,
                      });
                    }}
                    title="Edit"
                  >
                    ✏️
                  </button>
                </div>
              </>
            )}
          </div>
        ))}
      </div>

      {/* Paginación */}
      <div className="pagination">
        <button onClick={() => handlePageChange(page - 1)} disabled={page === 0}>
          Previous
        </button>
        <span>
          Page {page + 1} of {totalPages}
        </span>
        <button
          onClick={() => handlePageChange(page + 1)}
          disabled={page + 1 >= totalPages}
        >
          Next
        </button>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            {loadingTask ? (
              <p>Loading...</p>
            ) : selectedTask ? (
              <>
                <h2>{selectedTask.title}</h2>
                <p>{selectedTask.description}</p>
                <p>
                  <b>Status:</b>{" "}
                  {selectedTask.completed ? "✅ Completed" : "⏳ Pending"}
                </p>
              </>
            ) : (
              <p>Error loading task</p>
            )}
            <button onClick={closeModal}>Close</button>
          </div>
        </div>
      )}
    </div>
    );
}

export default App;
