package net.ooder.skill.management.lifecycle;

import net.ooder.skill.management.model.SkillDefinition;
import net.ooder.skill.management.model.SkillDefinition.SkillStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class SkillLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(SkillLifecycleManager.class);

    private final List<SkillLifecycleListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(SkillLifecycleListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            log.debug("[SkillLifecycleManager] Added listener: {}", listener.getClass().getSimpleName());
        }
    }

    public void removeListener(SkillLifecycleListener listener) {
        listeners.remove(listener);
        log.debug("[SkillLifecycleManager] Removed listener: {}", listener.getClass().getSimpleName());
    }

    public List<SkillLifecycleListener> getListeners() {
        return listeners;
    }

    public void notifySkillDiscovered(String skillId, SkillDefinition skill) {
        log.debug("[SkillLifecycle] DISCOVERED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillDiscovered(skillId, skill);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillDiscovered): {}", e.getMessage());
            }
        }
    }

    public void notifySkillMetadataLoaded(String skillId, Map<String, Object> metadata) {
        log.debug("[SkillLifecycle] METADATA_LOADED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillMetadataLoaded(skillId, metadata);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillMetadataLoaded): {}", e.getMessage());
            }
        }
    }

    public void notifySkillLoading(String skillId) {
        log.debug("[SkillLifecycle] LOADING - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillLoading(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillLoading): {}", e.getMessage());
            }
        }
    }

    public void notifySkillLoaded(String skillId, SkillDefinition skill) {
        log.debug("[SkillLifecycle] LOADED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillLoaded(skillId, skill);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillLoaded): {}", e.getMessage());
            }
        }
    }

    public void notifySkillLoadFailed(String skillId, Throwable error) {
        log.error("[SkillLifecycle] LOAD_FAILED - {}: {}", skillId, error.getMessage());
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillLoadFailed(skillId, error);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillLoadFailed): {}", e.getMessage());
            }
        }
    }

    public void notifySkillInitializing(String skillId) {
        log.debug("[SkillLifecycle] INITIALIZING - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillInitializing(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillInitializing): {}", e.getMessage());
            }
        }
    }

    public void notifySkillInitialized(String skillId) {
        log.debug("[SkillLifecycle] INITIALIZED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillInitialized(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillInitialized): {}", e.getMessage());
            }
        }
    }

    public void notifySkillInitFailed(String skillId, Throwable error) {
        log.error("[SkillLifecycle] INIT_FAILED - {}: {}", skillId, error.getMessage());
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillInitFailed(skillId, error);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillInitFailed): {}", e.getMessage());
            }
        }
    }

    public void notifySkillStarting(String skillId) {
        log.debug("[SkillLifecycle] STARTING - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillStarting(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillStarting): {}", e.getMessage());
            }
        }
    }

    public void notifySkillStarted(String skillId) {
        log.info("[SkillLifecycle] STARTED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillStarted(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillStarted): {}", e.getMessage());
            }
        }
    }

    public void notifySkillStartFailed(String skillId, Throwable error) {
        log.error("[SkillLifecycle] START_FAILED - {}: {}", skillId, error.getMessage());
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillStartFailed(skillId, error);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillStartFailed): {}", e.getMessage());
            }
        }
    }

    public void notifySkillStatusChanged(String skillId, SkillStatus oldStatus, SkillStatus newStatus) {
        log.debug("[SkillLifecycle] STATUS_CHANGED - {}: {} -> {}", skillId, oldStatus, newStatus);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillStatusChanged(skillId, oldStatus, newStatus);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillStatusChanged): {}", e.getMessage());
            }
        }
    }

    public void notifySkillIdle(String skillId) {
        log.debug("[SkillLifecycle] IDLE - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillIdle(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillIdle): {}", e.getMessage());
            }
        }
    }

    public void notifySkillRunning(String skillId) {
        log.debug("[SkillLifecycle] RUNNING - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillRunning(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillRunning): {}", e.getMessage());
            }
        }
    }

    public void notifySkillPaused(String skillId) {
        log.debug("[SkillLifecycle] PAUSED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillPaused(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillPaused): {}", e.getMessage());
            }
        }
    }

    public void notifySkillStopping(String skillId) {
        log.debug("[SkillLifecycle] STOPPING - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillStopping(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillStopping): {}", e.getMessage());
            }
        }
    }

    public void notifySkillStopped(String skillId) {
        log.info("[SkillLifecycle] STOPPED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillStopped(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillStopped): {}", e.getMessage());
            }
        }
    }

    public void notifySkillUnloading(String skillId) {
        log.debug("[SkillLifecycle] UNLOADING - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillUnloading(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillUnloading): {}", e.getMessage());
            }
        }
    }

    public void notifySkillUnloaded(String skillId) {
        log.info("[SkillLifecycle] UNLOADED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillUnloaded(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillUnloaded): {}", e.getMessage());
            }
        }
    }

    public void notifySkillError(String skillId, Throwable error) {
        log.error("[SkillLifecycle] ERROR - {}: {}", skillId, error.getMessage());
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillError(skillId, error);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillError): {}", e.getMessage());
            }
        }
    }

    public void notifySkillRecovered(String skillId) {
        log.info("[SkillLifecycle] RECOVERED - {}", skillId);
        for (SkillLifecycleListener listener : listeners) {
            try {
                listener.onSkillRecovered(skillId);
            } catch (Exception e) {
                log.error("[SkillLifecycleManager] Listener error (onSkillRecovered): {}", e.getMessage());
            }
        }
    }
}
