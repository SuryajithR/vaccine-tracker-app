<template>
  <div class="container">
    <div class="row two" style="margin-top:20px;">
      <div class="card">
        <h2 style="margin-top:0;">Add Immunization</h2>

        <div class="row">
          <select v-model="form.vaccineId">
            <option disabled value="">Select vaccine</option>
            <option v-for="v in vaccines" :key="v.id" :value="v.id">
              {{ v.name }} ({{ v.totalDoses }} dose)
            </option>
          </select>
          <!-- Dose number is auto-selected to enforce correct order. -->
          <input type="date" class="input" v-model="form.takenDate" :min="today" :max="today" readonly/>

          <button class="btn" @click="addRecord">Save</button>

          <p v-if="error" style="margin:0;color:var(--danger);font-weight:700">{{ error }}</p>
        </div>
      </div>

      <div class="card">
        <h2 style="margin-top:0;">Summary</h2>
        <div style="display:flex;gap:10px;flex-wrap:wrap;">
          <span class="badge ok">Taken: {{ records.length }}</span>
          <span class="badge warn">Due Soon: {{ dueSoonCount }}</span>
          <span class="badge bad">Overdue: {{ overdueCount }}</span>
        </div>

        <p style="color:var(--muted);margin-top:14px;">
          This vaccine tracker calculates next due date automatically using vaccine gap days.
        </p>
      </div>
    </div>


<!-- ✅ Records Card -->
<div class="card scrollableCard" style="margin-top:16px;">
  <div class="recordsHeader">
    <h2 class="recordsTitle">Your Immunization Records</h2>
    <p class="recordsSub">Grouped by vaccine in dose order (latest on top).</p>
  </div>

  <div v-if="records.length === 0" class="emptyState">
    No records yet. Add your first vaccine dose above.
  </div>

  <div v-else class="vaxGroups">
    <div
      v-for="(group, vaccineName) in groupedRecords"
      :key="vaccineName"
      class="vaxGroupCard"
    >
      <!-- Vaccine Group Header -->
      <div class="vaxGroupHeader">
        <div class="vaxName">{{ vaccineName }}</div>
        <div class="vaxCount">{{ group.length }} dose(s)</div>
      </div>

      <!-- Timeline -->
      <div class="timeline">
        <div
          v-for="r in group"
          :key="r.id"
          class="timelineItem"
        >
          <div class="timelineDot"></div>

          <div class="timelineContent">
            <!-- ✅ TOP ROW (Dose + Badge + Actions in same row) -->
            <div class="timelineTopRow">
              <div class="timelineLeftTop">
                <div class="timelineTitle">Dose {{ r.doseNumber }}</div>

                <span v-if="badgeText(r) !== 'Taken'" class="badge" :class="badgeClass(r)">
                  {{ badgeText(r) }}
                </span>

              </div>

              <div class="timelineActions">
                <!-- ✅ Completed → Completed pill + Delete -->
                <template v-if="!r.nextDueDate">
                  <button
                    class="btn secondary danger-outline"
                    @click="confirmDelete(r)"
                  >
                    Delete
                  </button>
                </template>

                <!-- ✅ Not completed → Finish + Delete -->
                <template v-else>
                  <button
                    v-if="canFinish(r)"
                    class="btn secondary"
                    @click="confirmFinish(r)"
                  >
                    Finish
                  </button>

                  <button
                    class="btn secondary danger-outline"
                    @click="confirmDelete(r)"
                  >
                    Delete
                  </button>
                </template>
              </div>
            </div>

            <!-- ✅ META INFO -->
            <div class="timelineMeta">
              <span class="metaLabel">Taken:</span>
              <span class="metaValue">{{ r.takenDate }}</span>

              <template v-if="r.nextDueDate">
                <span class="metaDivider">•</span>
                <span class="metaLabel">{{ dueLabel(r) }} Due:</span>
                <span class="metaValue">{{ r.nextDueDate }}</span>
              </template>
              <template v-else>
              </template>
            </div>
          </div>
        </div>
      </div>
      <!-- Timeline end -->
    </div>
  </div>
</div>

  </div>
</template>

<script>
import api from "../api";

export default {
    data() {
      const today = new Date();
      const localToday = new Date(today.getTime() - today.getTimezoneOffset() * 60000)
        .toISOString()
        .slice(0, 10);

      return {
        vaccines: [],
        records: [],
        form: {
          vaccineId: "",
          doseNumber: 1,
          takenDate: localToday
        },
        error: ""
      };
    },

  computed: {

    groupedRecords() {
      // group records by vaccine name
      const grouped = {};

      for (const r of this.records) {
        const name = r.vaccine?.name || "Unknown Vaccine";
        if (!grouped[name]) grouped[name] = [];
        grouped[name].push(r);
      }

      // sort doses so Dose 1 is bottom (ASC),
      // but UI timeline will show latest first (DESC)
      Object.keys(grouped).forEach(key => {
        grouped[key].sort((a, b) => b.doseNumber - a.doseNumber);
      });

      return grouped;
    },


    overdueCount() {
      const today = new Date().toISOString().slice(0, 10);

      return this.records.filter(r => {
        if (r.isClosed) return false;
        if (!r.nextDueDate) return false;

        // ✅ only latest record per vaccine
        return this.isLatestForVaccine(r) && r.nextDueDate < today;
      }).length;
    },


    dueSoonCount() {
      const now = new Date();
      const week = new Date(now);
      week.setDate(now.getDate() + 7);

      return this.records.filter(r => {
        if (!r.nextDueDate) return false;
        const d = new Date(r.nextDueDate);
        return d >= now && d <= week;
      }).length;
    },

    nextAllowedDose() {
      if (!this.form.vaccineId) return 1;

      const vaccineId = Number(this.form.vaccineId);
      const related = this.records
        .filter(r => r.vaccine.id === vaccineId)
        .sort((a, b) => b.doseNumber - a.doseNumber);

      if (related.length === 0) return 1;
      return related[0].doseNumber + 1;
    }
  },

  methods: {

    isLatestForVaccine(r) {
      const vaccineId = r.vaccine.id;

      const latest = this.records
        .filter(x => x.vaccine.id === vaccineId)
        .sort((a, b) => b.doseNumber - a.doseNumber)[0];

      return latest && latest.id === r.id;
    },


    dueDateClass(r) {
      if (!r.nextDueDate) return "";

      const today = new Date().toISOString().slice(0, 10);

      // overdue
      if (r.nextDueDate < today) return "bad";

      // due within 7 days
      const now = new Date();
      const week = new Date(now);
      week.setDate(now.getDate() + 7);
      const d = new Date(r.nextDueDate);

      if (d >= now && d <= week) return "warn";

      return "ok";
    },


    canFinish(r) {
      // Only show finish if record has nextDueDate (i.e. still active)
      if (!r.nextDueDate) return false;

      // Find latest record for this vaccine
      const vaccineId = r.vaccine.id;
      const latest = this.records
        .filter(x => x.vaccine.id === vaccineId)
        .sort((a, b) => b.doseNumber - a.doseNumber)[0];

      // Only latest dose should allow Finish
      return latest && latest.id === r.id;
    },


    async finishRecord(id) {
      await api.put(`/records/${id}/finish`);
      await this.loadData();
    },

    confirmFinish(r) {
      const ok = confirm(
        `Mark vaccine as Finished?\n\n${r.vaccine.name}\nThis will stop booster reminders.`
      );
      if (ok) this.finishRecord(r.id);
    },


    confirmDelete(r) {
      const ok = confirm(`Are you sure to Delete?\n\n${r.vaccine.name} - Dose ${r.doseNumber} taken on ${r.takenDate}`);
      if (ok) this.removeRecord(r.id);
    },

    dueLabel(r) {
        if (r.isClosed) return "Finished";
        if (!r.nextDueDate) return "";
        if (r.doseNumber < r.vaccine.totalDoses) return "Next Dose";
        return "Booster";
      },

    badgeText(r) {
      if (r.isClosed) return "Completed";
      if (!r.nextDueDate) return "Completed";

      // ✅ only show Overdue for the latest dose record
      if (!this.isLatestForVaccine(r)) return "Taken";

      const today = new Date().toISOString().slice(0, 10);
      if (r.nextDueDate < today) return "Overdue";
      return "Active";
    },

    badgeClass(r) {
      if (r.isClosed) return "ok";
      if (!r.nextDueDate) return "ok";

      // ✅ only latest record can be warn/bad
       if (!this.isLatestForVaccine(r)) return "ok";

      const today = new Date().toISOString().slice(0, 10);
      if (r.nextDueDate < today) return "bad";
      return "warn";
    },

    async loadData() {
      const [vaxRes, recRes] = await Promise.all([
        api.get("/vaccines"),
        api.get("/records")
      ]);
      this.vaccines = vaxRes.data;
      this.records = recRes.data;
    },

    async addRecord() {
      this.error = "";
      try {
        if (!this.form.vaccineId) {
          this.error = "Please select a vaccine.";
          return;
        }

        if (!this.form.doseNumber || this.form.doseNumber < 1) {
          this.error = "Dose number must be 1 or more.";
          return;
        }

        await api.post("/records", {
          vaccineId: this.form.vaccineId,
          doseNumber: this.nextAllowedDose,
          takenDate: this.form.takenDate
        });

        await this.loadData();
      } catch (e) {
        const msg = e?.response?.data?.message || "Failed to add record.";
        this.error = msg;
      }
    },

    async removeRecord(id) {
      await api.delete(`/records/${id}`);
      await this.loadData();
    }
  },

  async mounted() {
    try {
      await this.loadData();
    } catch (e) {
      localStorage.removeItem("token");
      this.$router.push("/login");
    }
  }
};
</script>

